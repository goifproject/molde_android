package com.limefriends.molde.model.repository.usecase;

import android.util.Log;

import com.limefriends.molde.common.FromSchemaToEntity;
import com.limefriends.molde.model.entity.comment.CommentEntity;
import com.limefriends.molde.model.entity.comment.ReportedCommentEntity;
import com.limefriends.molde.model.entity.news.CardNewsEntity;
import com.limefriends.molde.model.repository.Repository;
import com.limefriends.molde.networking.schema.response.Result;
import com.limefriends.molde.networking.service.MoldeRestfulService;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class CommentUseCase implements Repository.Comment {

    private final MoldeRestfulService.Comment mCommentService;
    private final MoldeRestfulService.CardNews mCardNewsService;
    private final FromSchemaToEntity mFromSchemaToEntity;

    public CommentUseCase(MoldeRestfulService.Comment commentService,
                          MoldeRestfulService.CardNews cardNewsService,
                          FromSchemaToEntity fromSchemaToEntity) {
        this.mCommentService = commentService;
        this.mCardNewsService = cardNewsService;
        this.mFromSchemaToEntity = fromSchemaToEntity;
    }


    @Override
    public Observable<List<CommentEntity>> getNewsComment(int newsId, int perPage, int page) {
        return mCommentService
                .getNewsCommentObservable(newsId, perPage, page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(commentResponseSchema -> {
                    List<CommentEntity> entities = mFromSchemaToEntity.commentNS(commentResponseSchema.getData());
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
        return mCommentService
                .getMyCommentObservable(userId, perPage, page)

                .flatMap(commentResponseSchema -> {
                    Log.e("Thread1", Thread.currentThread().getName());
                    List<CommentEntity> entities
                            = mFromSchemaToEntity.commentNS(commentResponseSchema.getData());
                    return entities == null ? Observable.empty() : Observable.fromIterable(entities);
                })
                .filter(commentEntity -> {

                    boolean parentNewsAbsent = true;

                    for (CardNewsEntity entity : lastEntities) {
                        if (entity.getNewsId() == commentEntity.getNewsId()) {
                            Log.e("Thread2 - skip", Thread.currentThread().getName());
                            parentNewsAbsent = false;
                            entity.addComments(commentEntity);
                            break;
                        }
                    }
                    return parentNewsAbsent;
                })
                .concatMap(commentEntity -> {
                    Log.e("Thread3", Thread.currentThread().getName());
                    // currentComment.add(commentEntity);
                    return mCardNewsService
                            .getCardNewsByIdObservable(commentEntity.getNewsId())
                            .map(cardNewsResponseSchema -> {
                                Log.e("Thread4", Thread.currentThread().getName());
                                CardNewsEntity newsEntity
                                        = mFromSchemaToEntity.cardNewsNS(cardNewsResponseSchema.getData().get(0));
//                                newsEntity.addComments(currentComment.get(0));
//                                currentComment.clear();
                                newsEntity.addComments(commentEntity);
                                lastEntities.add(newsEntity);
                                Log.e("Thread5", Thread.currentThread().getName());
                                return newsEntity;
                            });
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    @Override
    public Observable<CommentEntity> getReportedComment(int perPage, int page) {
        return mCommentService
                .getReportedCommentObservable(perPage, page)
                .subscribeOn(Schedulers.io())
                .flatMap(reportedCommentResponseSchema -> {
                    Log.e("Thread1", Thread.currentThread().getName());
                    List<ReportedCommentEntity> entities
                            = mFromSchemaToEntity.reportedCommentNS(reportedCommentResponseSchema.getData());
                    return entities == null ? Observable.empty() : Observable.fromIterable(entities);
                })
                .flatMap(reportedCommentEntity -> {
                    Log.e("Thread2", Thread.currentThread().getName());
                    return mCommentService
                            .getReportedCommentDetailObservable(reportedCommentEntity.getCommId())
                            .subscribeOn(Schedulers.io());
                })
                .filter(commentResponseSchema -> commentResponseSchema.getData().size() != 0)
                .flatMap(commentResponseSchema -> {
                    Log.e("Thread3", Thread.currentThread().getName());
                    return Observable.just(mFromSchemaToEntity.commentNS(commentResponseSchema.getData().get(0)));
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Result> createNewComment(String userId, String userName, int newsId, String content, String regiDate) {
        return mCommentService
                .createNewCommentObservable(userId, userName, newsId, content, regiDate)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Result> reportComment(String uId, int commentId) {
        return mCommentService
                .reportCommentObservable(uId, commentId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Result> deleteComment(int commentId) {
        return mCommentService
                .deleteCommentObservable(commentId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Result> deleteReportedComment(int commentUserId) {
        return mCommentService
                .deleteReportedCommentObservable(commentUserId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
