package com.data.entity.mapper;

import com.appster.core.adapter.DisplayableItem;
import com.data.entity.DailyTreatListInfoEntity;
import com.data.entity.TreatEntity;
import com.domain.models.TreatBigItem;
import com.domain.models.TreatListItemModel;
import com.domain.models.TreatMiniItem;
import com.domain.models.TreatUltimateItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thanhbc on 11/13/17.
 */

public class DailyTreatListInfoEntityMapper {

    public List<DisplayableItem> transform(List<DailyTreatListInfoEntity> treatInfos) {
        List<DisplayableItem> displayableTreats = null;
        if (treatInfos != null) {
            displayableTreats = new ArrayList<>();
            int treatSize = treatInfos.size();
            for (int i = 0; i < treatSize; i++) {
                DailyTreatListInfoEntity entity = treatInfos.get(i);
                displayableTreats.add(new TreatListItemModel(entity.title));
                if (i == 0) {
                    displayableTreats.addAll(transformUltimateTreats(entity.treats));
                } else if (i == 1) {
                    displayableTreats.addAll(transformBigTreats(entity.treats));
                } else {
                    displayableTreats.addAll(transformMiniTreats(entity.treats));
                }
            }
        }
        return displayableTreats;
    }

    private List<DisplayableItem> transformUltimateTreats(List<TreatEntity> treats) {
        List<DisplayableItem> ultimateItems = new ArrayList<>();
        if (treats == null) return ultimateItems;
        int treatSize = treats.size();
        for (int i = 0; i < treatSize; i++) {
            TreatEntity treat = treats.get(i);
            TreatUltimateItem item = new TreatUltimateItem.Builder()
                    .prizeImgUrl(treat.getImage())
                    .prizeName(treat.getTitle())
                    .prizeDesc(treat.getDescription())
                    .value(treat.getValue()).build();
            ultimateItems.add(item);
        }
        return ultimateItems;
    }

    private List<DisplayableItem> transformBigTreats(List<TreatEntity> treats) {
        List<DisplayableItem> bigItems = new ArrayList<>();
        if (treats == null) return bigItems;
        int treatSize = treats.size();
        for (int i = 0; i < treatSize; i++) {
            TreatEntity treat = treats.get(i);
            TreatBigItem item = new TreatBigItem.Builder()
                    .prizeImgUrl(treat.getImage())
                    .prizeName(treat.getTitle())
                    .prizeDesc(treat.getDescription())
                    .value(treat.getValue()).build();
            bigItems.add(item);
        }
        return bigItems;
    }

    private List<DisplayableItem> transformMiniTreats(List<TreatEntity> treats) {
        List<DisplayableItem> miniItems = new ArrayList<>();
        if (treats == null) return miniItems;
        int treatSize = treats.size();
        for (int i = 0; i < treatSize; i++) {
            TreatEntity treat = treats.get(i);
            TreatMiniItem item = new TreatMiniItem.Builder()
                    .prizeImgUrl(treat.getImage())
                    .prizeName(treat.getTitle())
                    .prizeDesc(treat.getDescription())
                    .value(treat.getValue()).build();
            miniItems.add(item);
        }
        return miniItems;
    }
}
