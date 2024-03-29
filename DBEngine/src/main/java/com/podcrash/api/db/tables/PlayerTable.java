package com.podcrash.api.db.tables;

import com.mongodb.async.SingleResultCallback;
import com.mongodb.client.model.*;
import com.mongodb.client.result.UpdateResult;
import com.podcrash.api.db.DBUtils;
import com.podcrash.api.db.MongoBaseTable;

import static com.mongodb.client.model.Filters.*;

import com.podcrash.api.db.pojos.Currency;
import com.podcrash.api.db.pojos.InvictaPlayer;
import com.podcrash.api.db.pojos.PojoHelper;
import org.bson.conversions.Bson;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PlayerTable extends MongoBaseTable {
    public PlayerTable() {
        super("players");
    }

    @Override
    public DataTableType getDataTableType() {
        return DataTableType.PLAYERS;
    }

    @Override
    public void createTable() {
        CompletableFuture<String> future = new CompletableFuture<>();
        getCollection().createIndex(Indexes.descending("uuid"), new IndexOptions().unique(true), (res, t) -> {
            DBUtils.handleThrowables(t);
            future.complete(res);
        });
        futureGuaranteeGet(future);
    }

    public CompletableFuture<Void> insert(UUID uuid) {
        InvictaPlayer player = PojoHelper.createInvictaPlayer(uuid);

        CompletableFuture<Void> future = new CompletableFuture<>();
        getCollection(InvictaPlayer.class).insertOne(player, (res, t) -> {
            DBUtils.handleDuplicateKeyException(t);
            future.complete(res);
        });

        return future;
    }

    public InvictaPlayer getPlayerDocumentSync(UUID uuid, String... fields) {
        CompletableFuture<InvictaPlayer> future = getPlayerDocumentAsync(uuid, fields);
        return futureGuaranteeGet(future);
    }

    public CompletableFuture<InvictaPlayer> getPlayerDocumentAsync(UUID uuid, String... fields) {
        CompletableFuture<InvictaPlayer> future = new CompletableFuture<>();
        SingleResultCallback<InvictaPlayer> callback = (invictaPlayer, t) -> {
            DBUtils.handleThrowables(t);
            future.complete(invictaPlayer);
        };

        Bson limit = Projections.fields(Projections.include(fields), Projections.excludeId());
        getCollection(InvictaPlayer.class)
            .find(eq("uuid", uuid))
            .projection(limit)
            .first(callback);
        return future;
    }

    public CompletableFuture<UpdateResult> incrementMoney(UUID uuid, double value){
        CompletableFuture<UpdateResult> res = new CompletableFuture<>();
        getCollection().updateOne(eq("uuid", uuid), Updates.inc("currency.gold", value), ((result, t) -> {
            DBUtils.handleThrowables(t);
            res.complete(result);
        }));

        return res;
    }

    public CompletableFuture<Currency> getCurrency(UUID uuid) {
        return getPlayerDocumentAsync(uuid, "currency").thenApplyAsync(InvictaPlayer::getCurrency);
    }
}
