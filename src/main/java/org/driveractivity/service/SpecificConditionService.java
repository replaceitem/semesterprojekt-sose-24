package org.driveractivity.service;

import lombok.Getter;
import org.driveractivity.entity.Activity;
import org.driveractivity.entity.ActivityType;
import org.driveractivity.entity.SpecificCondition;
import org.driveractivity.entity.SpecificConditionType;
import org.driveractivity.exception.SpecificConditionException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.driveractivity.entity.SpecificConditionType.*;
import static org.driveractivity.entity.SpecificConditionType.Condition.FT;
import static org.driveractivity.entity.SpecificConditionType.Condition.OUT_OF_SCOPE;
import static org.driveractivity.entity.SpecificConditionType.END_FT;

public class SpecificConditionService {
    @Getter
    private final ArrayList<SpecificCondition> specificConditions;
    private final ActivityService activityService;
    private final ArrayList<DriverServiceListener> listeners;
    private boolean applyRules;
    public SpecificConditionService(ActivityService activityService) {
        applyRules = true;
        specificConditions = new ArrayList<>();
        listeners = new ArrayList<>();
        this.activityService = activityService;
    }

    public void clear() {
        specificConditions.clear();
    }

    public ArrayList<SpecificCondition> addSpecificConditions(List<SpecificCondition> inputConditions) throws SpecificConditionException {
        if(!applyRules) {
            specificConditions.addAll(inputConditions);
            IntStream.range(0, activityService.getActivities().size()).forEach(i -> listeners.forEach(l -> l.onActivityUpdated(i)));
            return specificConditions;
        }

        SpecificCondition beginCondition = inputConditions.stream().filter(s -> s.getSpecificConditionType() == BEGIN_FT || s.getSpecificConditionType() == BEGIN_OUT_OF_SCOPE).findFirst().orElse(null);
        SpecificCondition endCondition = inputConditions.stream().filter(s -> s.getSpecificConditionType() == END_FT || s.getSpecificConditionType() == END_OUT_OF_SCOPE).findFirst().orElse(null);
        if(beginCondition != null && endCondition != null) {
            if(beginCondition.getTimestamp().isAfter(endCondition.getTimestamp())) {
                throw new SpecificConditionException("The BEGIN_FT or BEGIN_OUT_OF_SCOPE must take place before the END_FT or END_OUT_OF_SCOPE");
            }
        }

        //if input contains OUT OF SCOPE, it must contain both
        //also it should not be allowed to intersect with other out-of-scope conditions
        if(inputConditions.stream().anyMatch(i -> i.getSpecificConditionType().getCondition() == OUT_OF_SCOPE)) {
            if(hasIncompleteScopeConditions(inputConditions)) {
                throw new SpecificConditionException("If a BEGIN_OUT_OF_SCOPE is added, an END_OUT_OF_SCOPE must be added as well");
            } else {
                for(SpecificCondition s : specificConditions) {
                    if(s.getSpecificConditionType() == BEGIN_OUT_OF_SCOPE) {
                        if(isTimestampWithinRange(s, findNextSpecificConditionOfType(END_OUT_OF_SCOPE, s), inputConditions.getFirst().getTimestamp()) || isTimestampWithinRange(s, findNextSpecificConditionOfType(END_OUT_OF_SCOPE, s), inputConditions.getLast().getTimestamp()) ) {
                            throw new SpecificConditionException("The new OUT_OF_SCOPE conditions intersect with existing ones");
                        }
                    }
                }
            }
        }
        //if the conditions are of FT type
        if(inputConditions.stream().anyMatch(specificCondition -> specificCondition.getSpecificConditionType().getCondition() == FT)) {
            //if input is ONLY beginFT, make sure that it ends with the next DRIVING activity
            //if there is no following DRIVING activity, then this FT does not have an end, set the flag that it has no end
            if(beginCondition != null && endCondition == null) {
                Activity nextDriving = findNextDrivingActivity(beginCondition.getTimestamp());
                if(nextDriving != null) {
                    inputConditions.add(SpecificCondition.builder()
                            .specificConditionType(END_FT)
                            .timestamp(nextDriving.getStartTime())
                            .build());
                    endCondition = inputConditions.getLast();
                } else {
                    beginCondition.setWithoutEnd(true);
                }
            }

            //Make sure they don't intersect with other FT conditions
            for(SpecificCondition s : specificConditions) {
                if(s.getSpecificConditionType() == BEGIN_FT && !s.isWithoutEnd()) {
                    if(beginCondition != null && isTimestampWithinRange(s, findNextSpecificConditionOfType(END_FT, s), beginCondition.getTimestamp()) && endCondition != null && isTimestampWithinRange(s, findNextSpecificConditionOfType(END_FT, s), endCondition.getTimestamp())) {
                        throw new SpecificConditionException("The new FT conditions intersect with existing ones");
                    }
                }
            }

            //if input is beginFT with endFT, they may not have a driving activity in between
            //if this is the case, the endFT should be set to the start of the next driving activity
            if(beginCondition != null && endCondition != null) {
                Activity nextDriving = findNextDrivingActivity(beginCondition.getTimestamp());
                if(nextDriving != null) {
                    if(isTimestampWithinRange(beginCondition, endCondition, nextDriving.getStartTime())) {
                        endCondition.setTimestamp(nextDriving.getStartTime());
                    }
                }
            }

            //There may not be any further FT conditions added after an unclosed beginFT
            if(beginCondition != null) {
                SpecificCondition unclosedFT = specificConditions.stream()
                        .filter(specificCondition -> specificCondition.getSpecificConditionType().getCondition() == FT && specificCondition.isWithoutEnd())
                        .findFirst()
                        .orElse(null);
                if(unclosedFT != null && unclosedFT.getTimestamp().isBefore(beginCondition.getTimestamp())) {
                    throw new SpecificConditionException("Please close the unclosed FT before adding further FT conditions after it.");
                }
            }
            //If only an endCondition is added
            if(beginCondition == null && endCondition != null) {
                SpecificCondition lastFT = findUnclosedFTCondition();

                if(lastFT == null || lastFT.getTimestamp().isAfter(endCondition.getTimestamp())) {
                    throw new SpecificConditionException("If an END_FT is added, a BEGIN_FT must either already exist or be added before it.");
                } else {
                    lastFT.setWithoutEnd(false);
                }
            }
        }

        specificConditions.addAll(inputConditions);

        IntStream.range(0, activityService.getActivities().size()).forEach(i -> listeners.forEach(l -> l.onActivityUpdated(i)));
        return specificConditions;
    }
    public void addListener(DriverServiceListener listener) {
        listeners.add(listener);
    }

    public ArrayList<SpecificCondition> removeSpecificConditions(SpecificCondition inputCondition) {
        ArrayList<SpecificCondition> toDelete = new ArrayList<>();
        toDelete.add(inputCondition);
        SpecificCondition correspondingCondition;
        if(inputCondition.getSpecificConditionType().isBegin()) {
            correspondingCondition = findNextSpecificConditionOfType(inputCondition.getSpecificConditionType().getOpposite(), inputCondition);
        } else {
            correspondingCondition = findPreviousSpecificConditionOfType(inputCondition.getSpecificConditionType().getOpposite(), inputCondition);
        }
        if(correspondingCondition != null) {
            toDelete.add(correspondingCondition);
        }

        specificConditions.removeAll(toDelete);

        IntStream.range(0, activityService.getActivities().size()).forEach(i -> listeners.forEach(l -> l.onActivityUpdated(i)));
        return specificConditions;
    }

    private boolean isTimestampWithinRange(SpecificCondition begin, SpecificCondition end, LocalDateTime timestamp) {
        return (timestamp.isAfter(begin.getTimestamp()) || timestamp.isEqual(begin.getTimestamp())) && (timestamp.isBefore(end.getTimestamp()) || timestamp.isEqual(end.getTimestamp()));
    }

    private boolean hasIncompleteScopeConditions(List<SpecificCondition> inputConditions) {
        return inputConditions.stream().filter(s -> s.getSpecificConditionType() == BEGIN_OUT_OF_SCOPE).count() != inputConditions.stream().filter(s -> s.getSpecificConditionType() == END_OUT_OF_SCOPE).count();
    }

    private SpecificCondition findUnclosedFTCondition() {
        return specificConditions.stream()
                .filter(SpecificCondition::isWithoutEnd)
                .findFirst()
                .orElse(null);
    }

    private SpecificCondition findNextSpecificConditionOfType(SpecificConditionType type, SpecificCondition inputCondition) {
        for(SpecificCondition specificCondition : specificConditions) {
            if(specificCondition.getSpecificConditionType() == type && (specificCondition.getTimestamp().isAfter(inputCondition.getTimestamp()) || specificCondition.getTimestamp().isEqual(inputCondition.getTimestamp()))) {
                return specificCondition;
            }
        }
        return null;
    }
    private SpecificCondition findPreviousSpecificConditionOfType(SpecificConditionType type, SpecificCondition inputCondition) {
        SpecificCondition previousCondition = specificConditions.getFirst();
        if(previousCondition == null) {
            return null;
        }
        for(SpecificCondition specificCondition : specificConditions) {
            if(specificCondition.getTimestamp().isBefore(inputCondition.getTimestamp()) || specificCondition.getTimestamp().isEqual(inputCondition.getTimestamp()) && specificCondition.getSpecificConditionType() == type) {
                if(specificCondition.getTimestamp().isAfter(previousCondition.getTimestamp())) {
                    previousCondition = specificCondition;
                }
            }
        }
        return previousCondition;
    }

    private Activity findNextDrivingActivity(LocalDateTime timestamp) {
        for(Activity activity : activityService.getActivities()) {
            if(activity.getType() == ActivityType.DRIVING && (activity.getStartTime().isAfter(timestamp) || activity.getStartTime().isEqual(timestamp))) {
                return activity;
            }
        }
        return null;
    }


    public void setRulesEnabled(boolean enabled) {
        applyRules = enabled;
    }
}
