package br.com.mobile2you.m2ybase.data.local;

/**
 * Created by mayerlevy on 10/30/17.
 */

public class PGPManagerSingleton {
    private static PGPManager holder;

    public static void initialize(PGPManager pgpManager) {
        holder = pgpManager;
    }
    public static PGPManager getInstance() {return holder;}
}
