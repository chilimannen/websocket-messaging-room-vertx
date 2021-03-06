package com.websocket.frontend.room;

import com.websocket.frontend.room.Protocol.Authenticate;
import com.websocket.frontend.room.Protocol.Serializer;
import com.websocket.frontend.room.Protocol.Token;

/**
 * Created by Robin on 2015-12-22.
 * <p>
 * Handles user authentication.
 */
enum AuthenticationHandler {
    AUTHENTICATE() {
        @Override
        public void invoke(Parameters params) {
            Authenticate authenticate = (Authenticate) Serializer.unpack(params.data, Authenticate.class);

            if (!params.client.isAuthenticated()) {
                authenticate.getHeader().setActor(params.client.getId());
                params.handler.sendBus(Configuration.UPSTREAM, authenticate);
            }
        }
    },

    AUTHENTICATE_TOKEN() {
        @Override
        public void invoke(Parameters params) {
            Token token = (Token) Serializer.unpack(params.data, Token.class);

            if (Authentication.VerifyToken(token)) {
                params.client.setUsername(token.getUsername());
                params.client.setAuthenticated(true);
                params.handler.sendBus(params.getAddress(), new Token(true));
            } else
                params.handler.sendBus(params.getAddress(), new Token(false));
        }
    };

    public abstract void invoke(Parameters params);
}