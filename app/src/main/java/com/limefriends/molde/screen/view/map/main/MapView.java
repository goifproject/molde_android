package com.limefriends.molde.screen.view.map.main;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.limefriends.molde.model.entity.feed.FeedEntity;
import com.limefriends.molde.screen.common.view.ObservableView;

import java.util.List;

// #1
// 뷰는 컨트롤러의 상태에 의존해서도 안 되고(authority 등)
// 컨트롤러도 특정 뷰가 존재하는지 알아서도 안 된다. 메시지를 띄워달라, 진행경과를 보여달라고 해야지 프로그래스바를 띄워주라는 등
// 혹은 스낵바를 띄워달라는 것은 바람직하지 않음. 서로 구체적인 업무를 알아서는 안 되고 추상 업무만 주고받아야 함
// #2
// 생각이 좀 바뀜. 컨트롤러는 뷰를 옵저빙 하는 대상으로 뷰에 대해서 구체적인 요청을 할 수 있지만 뷰는 구체적인 컨트롤러 어떤 것도
// 알아서는 안 됨. 쉽게 생각하면 뷰를 여러 곳에서 사용한다고 생각해보자. 그렇게 구체적인 코드 못 사용할걸..
// #3
// 뷰는 데이터를 가지고 있으면 안 됨. 상태값도 가지고 있으면 안 됨. 상태를 넘겨줘서 분기하거자 하지 않고 분기는 컨트롤러에서 하고
// 뷰는 멀뚱멀뚱하게 서 있다가 컨트롤러가 시키는 뷰만 보여주거나 눌렸는지 알려주도록 한다
// #4
// 그러면 마커 클릭 리스너처럼 마커를 시스템에서 넘겨주는 경우가 있는데 이는 똑같이 적용해보면 컨트롤러가 보내줄 것을 단지
// 시스템 혹은 라이브러리에서 자동으로 호출을 해 준 것 뿐이. 따라서 마커를 초기에 설정할 때 여러 데이터를 넣는 것을 허용하도록 하자
// 마커도 하나이 데이터 객체라고 생각한다면
// #5
// 뷰는 절대 컨트롤러를 알아서는 안 됨. 따라서 데이터를 직접적으로 가져오는 함수를 두기 보다는 어떤 해위에 대한 호출, 즉
// 콜백함수를 둬야 한다. 따라서 모든 함수는 on~이렇게 적용해야 함
// #6
// 뷰에서 데이터를 가져다 사용하는 방법은 없는 데이터를 가져오는 방법일 수도 있고, 한 번 데이터를 불러왔다면 캐시에서
// 가져오는 방법일 수 있다. 캐시에서 가져오면 시시해 보여서 굳이 이렇게 해야 하나 할 수 있으나 일관성을 지키자
// #7
// 마커는 뷰와 컨트롤러 두 역할을 하기 때문에 뷰, 컨트롤러 두 곳에서 다뤄줌

public interface MapView extends ObservableView<MapView.Listener> {

    /**
     * 뷰는 컨트롤러에게 이런 콜백 함수를 전달하고 결과가 없으면 void로 상관 안하고 화면에 데이터를 경우 리턴값을 내어준다
     */
    interface Listener {

        boolean onMapLongClicked();

        void onSearchBarClicked();

        void onFavoriteButtonClicked();

        void onReportButtonClicked();

        void onFindCurrentLocationClicked();

        void onReAskPermissionAllowed();

        Marker onPageSelected(int position);

        void onLastPageSelected();

        void onReportCardItemClicked(int feedId);
    }

    /**
     * show
     */

    void showSnackBar(String message);

    void showCardView();

    void showProgressIndication();

    void hideProgressIndication();

    void showAskAgainDialog();

    /**
     * view control
     */

    boolean isReportCardShown();

    void moveCamera(LatLng latLng, int zoom);

    void enlargeMarkerIcon(Marker marker, int status);

    Marker addMarker(LatLng latLng, String title, String snippet, int icon, int tag);

    void clearMap();

    /**
     * bind data
     */

    void bindFeed(List<FeedEntity> feedEntities);

    void bindSearchLocation(String location);

    void clearReportCardList();
}
