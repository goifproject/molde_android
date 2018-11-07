package com.limefriends.molde.model.repository.usecase;

import com.limefriends.molde.model.common.FromSchemaToEntity;
import com.limefriends.molde.networking.common.NetworkHelper;
import com.limefriends.molde.screen.common.toastHelper.ToastHelper;

/**
 * controller 는 네트워크에 관한 정보를 알 필요가 없다. 따라서 네트워크 관련 처리를 해주는 네트워크 관련
 * UseCase 에서 네트워크 연결을 처리해준다
 */
abstract class BaseNetworkUseCase {

    private FromSchemaToEntity mFromSchemaToEntity;
    private ToastHelper mToastHelper;


    private final NetworkHelper mNetworkHelper;

    BaseNetworkUseCase(ToastHelper toastHelper,
                       NetworkHelper networkHelper) {
        this.mToastHelper = toastHelper;
        this.mNetworkHelper = networkHelper;
    }

    BaseNetworkUseCase(FromSchemaToEntity fromSchemaToEntity,
                       ToastHelper toastHelper,
                       NetworkHelper networkHelper) {
        this.mFromSchemaToEntity = fromSchemaToEntity;
        this.mToastHelper = toastHelper;
        this.mNetworkHelper = networkHelper;
    }

    boolean isNetworkConnected() {
        if (!mNetworkHelper.isNetworkConnected()) {
            mToastHelper.showNoNetworkError();
            return false;
        }
        return true;
    }

    FromSchemaToEntity getFromSchemaToEntity() {
        return mFromSchemaToEntity;
    }


}
