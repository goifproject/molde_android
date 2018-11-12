package com.limefriends.molde.common.di;

import com.limefriends.molde.model.repository.Repository;
import com.limefriends.molde.screen.common.bottomNavigationViewHelper.BottomNavigationViewHelper;
import com.limefriends.molde.screen.common.dialog.DialogFactory;
import com.limefriends.molde.screen.common.dialog.DialogManager;
import com.limefriends.molde.screen.common.imageLoader.ImageLoader;
import com.limefriends.molde.screen.common.screensNavigator.ActivityScreenNavigator;
import com.limefriends.molde.screen.common.screensNavigator.FragmentScreenNavigator;
import com.limefriends.molde.screen.common.toastHelper.ToastHelper;
import com.limefriends.molde.screen.common.view.ViewFactory;
import com.limefriends.molde.screen.common.viewController.BackPressDispatcher;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class Injector {

    private final PresentationCompositionRoot mPresentationCompositionRoot;

    public Injector(PresentationCompositionRoot mPresentationCompositionRoot) {
        this.mPresentationCompositionRoot = mPresentationCompositionRoot;
    }

    public void inject(Object client) {
        Class clazz = client.getClass();

        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            if (isAnnotatedForInjection(field)) {
                injectField(client, field);
            }
        }
    }

    private boolean isAnnotatedForInjection(Field field) {

        Annotation[] annotations = field.getDeclaredAnnotations();

        for (Annotation annotation : annotations) {
            if (annotation instanceof Service) {
                return true;
            }
        }

        return false;
    }

    private void injectField(Object client, Field field) {
        try {
            boolean isAccessibleInitially = field.isAccessible();
            field.setAccessible(true);
            field.set(client, getServiceForClass(field.getType()));
            field.setAccessible(isAccessibleInitially);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private Object getServiceForClass(Class<?> type) {
        if (type.equals(Repository.Comment.class)) {
            return mPresentationCompositionRoot.getCommentUseCase();
        }
        else if (type.equals(Repository.Faq.class)) {
            return mPresentationCompositionRoot.getFaqUseCase();
        }
        else if (type.equals(Repository.Favorite.class)) {
            return mPresentationCompositionRoot.getFavoriteUseCase();
        }
        else if (type.equals(Repository.Feed.class)) {
            return mPresentationCompositionRoot.getFeedUseCase();
        }
        else if (type.equals(Repository.FeedResult.class)) {
            return mPresentationCompositionRoot.getFeedResultUseCase();
        }
        else if (type.equals(Repository.CardNews.class)) {
            return mPresentationCompositionRoot.getCardNewsUseCase();
        }
        else if (type.equals(Repository.Safehouse.class)) {
            return mPresentationCompositionRoot.getSafehouseUseCase();
        }
        else if (type.equals(Repository.Scrap.class)) {
            return mPresentationCompositionRoot.getScrapUseCase();
        }
        else if (type.equals(Repository.SearchLocation.class)) {
            return mPresentationCompositionRoot.getSearchLocationUseCase();
        }
        else if (type.equals(ActivityScreenNavigator.class)) {
            return mPresentationCompositionRoot.getActivityScreenNavigator();
        }
        else if (type.equals(FragmentScreenNavigator.class)) {
            return mPresentationCompositionRoot.getFragmentScreenNavigator();
        }
        else if (type.equals(ToastHelper.class)) {
            return mPresentationCompositionRoot.getToastHelper();
        }
        else if (type.equals(BottomNavigationViewHelper.class)) {
            return mPresentationCompositionRoot.getBottomNavigationViewHelper();
        }
        else if (type.equals(DialogManager.class)) {
            return mPresentationCompositionRoot.getDialogManager();
        }
        else if (type.equals(DialogFactory.class)) {
            return mPresentationCompositionRoot.getDialogFactory();
        }
        else if (type.equals(ViewFactory.class)) {
            return mPresentationCompositionRoot.getViewFactory();
        }
        else if (type.equals(ImageLoader.class)) {
            return mPresentationCompositionRoot.getImageLoader();
        }
        else if (type.equals(BackPressDispatcher.class)) {
            return mPresentationCompositionRoot.getBackPressDispatcher();
        }
        else {
            throw new RuntimeException("unsupported service type class: "+type);
        }
    }



}
