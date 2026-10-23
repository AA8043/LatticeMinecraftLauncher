package org.a8043.lmcl.game.user;

public abstract class Authenticator<T extends Credentials> {
    public abstract AccountData authenticate(T credentials) throws Exception;
}
