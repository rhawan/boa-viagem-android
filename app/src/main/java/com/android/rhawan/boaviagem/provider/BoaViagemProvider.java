package com.android.rhawan.boaviagem.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.android.rhawan.boaviagem.util.DatabaseHelper;

import static com.android.rhawan.boaviagem.provider.BoaViagemContract.AUTHORITY;
import static com.android.rhawan.boaviagem.provider.BoaViagemContract.GASTO_PATH;
import static com.android.rhawan.boaviagem.provider.BoaViagemContract.Gasto;
import static com.android.rhawan.boaviagem.provider.BoaViagemContract.VIAGEM_PATH;
import static com.android.rhawan.boaviagem.provider.BoaViagemContract.Viagem;

/**
 * Created by Rhawan on 14/08/2016.
 * Content Provider da aplicação, pode ser realizado as seguintes operações:
 *
 * //inserir ou pesquisar viagens
 * content://com.android.rhawan.boaviagem.provider/viagem
 *
 * //atualizar ou remover viagem
 * content://com.android.rhawan.boaviagem.provider/viagem/#
 *
 * //pesquisar gastos de uma viagem
 * content://com.android.rhawan.boaviagem.provider/gasto/viagem/#
 *
 * //inserir ou pesquisar gastos
 * content://com.android.rhawan.boaviagem.provider/gasto
 *
 * //atualizar ou remover gasto
 * content://com.android.rhawan.boaviagem.provider/gasto/#
 *
 */
public class BoaViagemProvider extends ContentProvider {

    private DatabaseHelper databaseHelper;

    public static final int VIAGENS = 1;
    public static final int VIAGEM_ID = 2;
    public static final int GASTOS = 3;
    public static final int GASTO_ID = 4;
    public static final int GASTOS_VIAGEM_ID = 5;

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(AUTHORITY, VIAGEM_PATH, VIAGENS);
        uriMatcher.addURI(AUTHORITY, VIAGEM_PATH + "/#", VIAGEM_ID);
        uriMatcher.addURI(AUTHORITY, GASTO_PATH, GASTOS);
        uriMatcher.addURI(AUTHORITY, GASTO_PATH + "/#", GASTO_ID);
        uriMatcher.addURI(AUTHORITY, GASTO_PATH + "/" + VIAGEM_PATH + "/#", GASTOS_VIAGEM_ID);
    }

    @Override
    public boolean onCreate() {
        databaseHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        SQLiteDatabase database = databaseHelper.getReadableDatabase();
        switch (uriMatcher.match(uri)) {

            case VIAGENS:
                return database.query(VIAGEM_PATH, projection, selection, selectionArgs, null,
                        null, sortOrder);
            case VIAGEM_ID:
                selection = Viagem._ID + " = ?";
                selectionArgs = new String[]{ uri.getLastPathSegment() };
                return database.query(VIAGEM_PATH, projection, selection,
                        selectionArgs, null, null, sortOrder);
            case GASTOS:
                return database.query(GASTO_PATH, projection, selection,
                        selectionArgs, null, null, sortOrder);
            case GASTO_ID:
                selection = Gasto._ID + " = ?";
                selectionArgs = new String[]{ uri.getLastPathSegment() };
                return database.query(GASTO_PATH, projection, selection,
                        selectionArgs, null, null, sortOrder);
            case GASTOS_VIAGEM_ID:
                selection = Gasto.VIAGEM_ID + " = ?";
                selectionArgs = new String[]{ uri.getLastPathSegment() };
                return database.query(GASTO_PATH, projection, selection,
                        selectionArgs, null, null, sortOrder);
            default:
                throw new IllegalArgumentException("Uri desconhecida");
        }
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case VIAGENS:
                return Viagem.CONTENT_TYPE;
            case VIAGEM_ID:
                return Viagem.CONTENT_TYPE_ITEM;
            case GASTOS:
                return Gasto.CONTENT_TYPE;
            case GASTO_ID:
                return Gasto.CONTENT_TYPE_ITEM;
            case GASTOS_VIAGEM_ID:
                return Gasto.CONTENT_TYPE;
            default:
                throw new IllegalArgumentException("Uri desconhecida");
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        long id;

        switch (uriMatcher.match(uri)) {
            case VIAGENS:
                id = database.insert(VIAGEM_PATH, null, values);
                return Uri.withAppendedPath(Viagem.CONTENT_URI, String.valueOf(id));
            case GASTOS:
                id = database.insert(GASTO_PATH, null, values);
                return Uri.withAppendedPath(Gasto.CONTENT_URI, String.valueOf(id));
            default:
                throw new IllegalArgumentException("Uri desconhecida");
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = databaseHelper.getWritableDatabase();

        switch (uriMatcher.match(uri)) {
            case VIAGEM_ID:
                selection = Viagem._ID + " = ?";
                selectionArgs = new String[]{ uri.getLastPathSegment() };
                return database.delete(VIAGEM_PATH, selection, selectionArgs);
            case GASTO_ID:
                selection = Gasto._ID + " = ?";
                selectionArgs = new String[]{ uri.getLastPathSegment() };
                return database.delete(GASTO_PATH, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Uri desconhecida");
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase database = databaseHelper.getWritableDatabase();

        switch (uriMatcher.match(uri)) {
            case VIAGEM_ID:
                selection = Viagem._ID + " = ?";
                selectionArgs = new String[]{ uri.getLastPathSegment() };
                return database.delete(VIAGEM_PATH, selection, selectionArgs);
            case GASTO_ID:
                selection = Gasto._ID + " = ?";
                selectionArgs = new String[]{ uri.getLastPathSegment() };
                return database.delete(GASTO_PATH, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Uri desconhecida");
        }
    }
}
