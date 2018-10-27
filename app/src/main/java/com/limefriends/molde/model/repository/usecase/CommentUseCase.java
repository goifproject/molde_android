package com.limefriends.molde.model.repository.usecase;

import com.limefriends.molde.model.repository.FromSchemaToEntity;
import com.limefriends.molde.model.entity.comment.CommentEntity;
import com.limefriends.molde.model.entity.comment.ReportedCommentEntity;
import com.limefriends.molde.model.entity.news.CardNewsEntity;
import com.limefriends.molde.model.repository.Repository;
import com.limefriends.molde.networking.NetworkHelper;
import com.limefriends.molde.networking.schema.response.Result;
import com.limefriends.molde.networking.service.MoldeRestfulService;
import com.limefriends.molde.screen.common.toastHelper.ToastHelper;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class CommentUseCase extends BaseNetworkUseCase implements Repository.Comment {

    private final MoldeRestfulService.Comment mCommentService;
    private final MoldeRestfulService.CardNews mCardNewsService;

    public CommentUseCase(MoldeRestfulService.Comment commentService,
                          MoldeRestfulService.CardNews cardNewsService,
                          FromSchemaToEntity fromSchemaToEntity,
                          ToastHelper toastHelper,
                          NetworkHelper networkHelper) {
        super(fromSchemaToEntity, toastHelper, networkHelper);

        this.mCommentService = commentService;
        this.mCardNewsService = cardNewsService;
    }


    @Override
    public Observable<List<CommentEntity>> getNewsComment(int newsId, int perPage, int page) {

        if (!isNetworkConnected()) return Observable.empty();

        return mCommentService
                .getNewsCommentObservable(newsId, perPage, page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(commentResponseSchema -> {
                    List<CommentEntity> entities
                            = getFromSchemaToEntity().commentNS(commentResponseSchema.getData());
                    return entities == null ? Observable.empty() : Observable.just(entities);
                });
    }

    /**
     * Q1 - 리스트로 다 만들어서 넘겨주고 싶은데 어떻게 해야 할까
     * Q2 - 백그라운드에서 동작은 하는데 스레드 내부에서 동기 방식으로 작동한다. 여러 스레드로 나눠서 호출하는 방법이 있지 않을까
     * <p>
     * 도전1 - 그냥 10개 전부 불러온 다음에 집어 넣을 때 검사하기
     * 도전2 - 멀티 스레딩은 적용 해야 하기 때문에 RxJava 동시성 처리하기 이슈 찾아보거나 블락킹 어떻게 하는지 참고하기
     */
    @Override
    public Observable<CardNewsEntity> getMyComment(List<CardNewsEntity> lastEntities,
                                                   String userId, int perPage, int page) {

        if (!isNetworkConnected()) return Observable.empty();

        return mCommentService
                .getMyCommentObservable(userId, perPage, page)

                .flatMap(commentResponseSchema -> {
                    List<CommentEntity> entities
                            = getFromSchemaToEntity().commentNS(commentResponseSchema.getData());
                    return entities == null ? Observable.empty() : Observable.fromIterable(entities);
                })
                .filter(commentEntity -> {

                    boolean parentNewsAbsent = true;

                    for (CardNewsEntity entity : lastEntities) {
                        if (entity.getNewsId() == commentEntity.getNewsId()) {
                            parentNewsAbsent = false;
                            entity.addComments(commentEntity);
                            break;
                        }
                    }
                    return parentNewsAbsent;
                })
                .concatMap(commentEntity ->
                        mCardNewsService
                                .getCardNewsByIdObservable(commentEntity.getNewsId())
                                .map(cardNewsResponseSchema -> {
                                    CardNewsEntity newsEntity
                                            = getFromSchemaToEntity().cardNewsNS(cardNewsResponseSchema.getData().get(0));
                                    newsEntity.addComments(commentEntity);
                                    lastEntities.add(newsEntity);
                                    return newsEntity;
                                }))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    @Override
    public Observable<CommentEntity> getReportedComment(int perPage, int page) {

        if (!isNetworkConnected()) return Observable.empty();

        return mCommentService
                .getReportedCommentObservable(perPage, page)
                .subscribeOn(Schedulers.io())
                .flatMap(reportedCommentResponseSchema -> {
                    List<ReportedCommentEntity> entities
                            = getFromSchemaToEntity().reportedCommentNS(reportedCommentResponseSchema.getData());
                    return entities == null ? Observable.empty() : Observable.fromIterable(entities);
                })
                .flatMap(reportedCommentEntity ->
                        mCommentService
                                .getReportedCommentDetailObservable(reportedCommentEntity.getCommId())
                                .subscribeOn(Schedulers.io()))
                .filter(commentResponseSchema -> commentResponseSchema.getData().size() != 0)
                .flatMap(commentResponseSchema ->
                        Observable.just(getFromSchemaToEntity().commentNS(commentResponseSchema.getData().get(0))))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Result> createNewComment(String userId, String userName, int newsId, String content, String regiDate) {

        if (!isNetworkConnected()) return Observable.empty();

        return mCommentService
                .createNewCommentObservable(userId, userName, newsId, content, regiDate)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Result> reportComment(String uId, int commentId) {

        if (!isNetworkConnected()) return Observable.empty();

        return mCommentService
                .reportCommentObservable(uId, commentId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Result> deleteComment(int commentId) {

        if (!isNetworkConnected()) return Observable.empty();

        return mCommentService
                .deleteCommentObservable(commentId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Result> deleteReportedComment(int commentUserId) {

        if (!isNetworkConnected()) return Observable.empty();

        return mCommentService
                .deleteReportedCommentObservable(commentUserId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
