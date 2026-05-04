package com.jpromi.darts.backend.services.impl;

import com.jpromi.darts.backend.entities.Achievement;
import com.jpromi.darts.backend.entities.DartThrow;
import com.jpromi.darts.backend.enums.DartThrowMultiplierEnum;
import com.jpromi.darts.backend.enums.ThrowType;
import com.jpromi.darts.backend.services.CheckAchievementService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CheckAchievementServiceImpl implements CheckAchievementService {

    @Override
    public Boolean checkAchievement(String condition, List<DartThrow> dartThrows) {
        // create temporary copy of throws
        ArrayList<DartThrow> _dartThrows = new ArrayList<>(dartThrows);

        // get all conditions
        String[] conditions = condition.split(";");

        // add counter
        Long countFound = 0L;

        // iterate over all conditions
        for (String c : conditions) {

            // split condition into search and value
            String[] searchAndValue = c.split(":");

            // check if search is ANY
            if (searchAndValue[0].equals("ANY")) {
                String value = searchAndValue[1];

                // iterate over all throws
                for (int i = 0; i < _dartThrows.size(); i++) {
                    DartThrow dartThrow = _dartThrows.get(i);
                    String search = convertMultiplier(dartThrow.getMultiplier()) + dartThrow.getScore();

                    // check if search is equal to value
                    if (search.equals(value)) {
                        _dartThrows.remove(dartThrow);
                        countFound++;
                        break;
                    }
                }
            }
        }
        if (countFound == conditions.length) {
            return true;
        } else {
            return false;
        }
    }

    private String convertMultiplier(DartThrowMultiplierEnum multiplier) {
        switch (multiplier) {
            case DartThrowMultiplierEnum.SINGLE:
                return "S";
            case DartThrowMultiplierEnum.DOUBLE:
                return "D";
            case DartThrowMultiplierEnum.TRIPLE:
                return "T";
            default:
                return "S";
        }
    }
}
