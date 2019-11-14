package com.data.entity.mapper;

import com.appster.core.adapter.DisplayableItem;
import com.data.entity.DailyBonusCheckDaysEntity;
import com.data.entity.TreatCollectEntity;
import com.data.entity.TreatEntity;
import com.domain.models.DailyBonusCheckDaysModel;
import com.domain.models.TreatCollectModel;
import com.domain.models.TreatItemModel;

import java.util.Arrays;
import java.util.List;

/**
 * Created by thanhbc on 11/13/17.
 */

public class TreatItemEntityMapper {
    public TreatItemEntityMapper() {
    }

    public List<DisplayableItem> transform(List<TreatEntity> treatEntities) {
        List<DisplayableItem> displayableTreatItems = null;
        if (treatEntities != null) {
            TreatItemModel[] emptyItems = new TreatItemModel[9];
            Arrays.fill(emptyItems, new TreatItemModel(true));
            displayableTreatItems = Arrays.asList(emptyItems);
            int treatEntitySize = treatEntities.size();
            for (int i = 0; i < treatEntitySize; i++) {
                TreatEntity entity = treatEntities.get(i);
                if (entity != null) {
                    displayableTreatItems.set(entity.getPosition(), transform(entity));
                }
            }
        }
        return displayableTreatItems;
    }

    public TreatItemModel transform(TreatEntity entity) {
        TreatItemModel treatItemModel = null;
        if (entity != null) {
            treatItemModel = new TreatItemModel.Builder<>()
                    .treatColor(entity.getTreatColor())
                    .treatId(entity.getId())
                    .prizeName(entity.getTitle())
                    .prizeImgUrl(entity.getImage())
                    .prizeRank(entity.getTreatRank())
                    .prizeDesc(entity.getDescription())
                    .prizeAmount(entity.getAmount())
                    .title(entity.getTitle())
                    .isClaimed(false).build();
        }

        return treatItemModel;
    }

    public TreatItemModel transformClaimedTreat(TreatEntity entity) {
        TreatItemModel treatItemModel = null;
        if (entity != null) {
            treatItemModel = transform(entity);
            treatItemModel.isClaimed = true;
        }

        return treatItemModel;
    }

    public DailyBonusCheckDaysModel transform(DailyBonusCheckDaysEntity entity) {
        if (entity != null) {
            DailyBonusCheckDaysModel model = new DailyBonusCheckDaysModel();
            model.setDayType(entity.getDayType());
            model.setClaimed(entity.getClaimed());
            return model;
        }

        return null;
    }

    public TreatCollectModel transform(TreatCollectEntity entity) {
        if (entity != null) {
            return new TreatCollectModel(
                    entity.getId(),
                    entity.getTitle(),
                    entity.getDescription(),
                    entity.getImage(),
                    entity.getAmount(),
                    entity.getTreatRank(),
                    false
            );
        }
        return null;
    }
}
