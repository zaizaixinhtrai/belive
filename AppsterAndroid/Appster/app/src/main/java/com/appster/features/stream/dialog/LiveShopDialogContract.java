package com.appster.features.stream.dialog;

import androidx.annotation.Nullable;

import com.appster.features.mvpbase.BaseContract;
import com.appster.models.ProductModel;

import java.util.List;

/**
 * Created by linh on 20/10/2017.
 */

public interface LiveShopDialogContract {

    interface View extends BaseContract.View{
        void onGetProductDetailSuccessfully(@Nullable List<ProductModel> data);
        void onGetProductDetailFailed(String message);
    }

    interface UserActions extends BaseContract.Presenter<View>{
        void getProductDetail(String sellerId);
    }
}
