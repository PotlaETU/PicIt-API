//package com.picit.config;
//
//
//import com.picit.iam.auth.JwtUtil;
//import com.picit.iam.repository.UserRepository;
//import lombok.AllArgsConstructor;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.Ordered;
//import org.springframework.core.annotation.Order;
//import org.springframework.messaging.Message;
//import org.springframework.messaging.MessageChannel;
//import org.springframework.messaging.simp.stomp.StompCommand;
//import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
//import org.springframework.messaging.support.ChannelInterceptor;
//import org.springframework.messaging.support.MessageHeaderAccessor;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
//
//@Configuration
//@EnableWebSocketMessageBroker
//@Order(Ordered.HIGHEST_PRECEDENCE + 1)
//@AllArgsConstructor
//public class WebSocketConfig implements ChannelInterceptor {
//
//    private final JwtUtil jwtUtils;
//    private final UserRepository userRepository;
//
//    @Override
//    public Message<?> preSend(Message<?> message, MessageChannel channel) {
//        final StompHeaderAccessor accessor =
//                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
//        if (StompCommand.CONNECT == accessor.getCommand()) {
//
//            //TODO: Add logic to check if the user is authenticated
//            String jwt = jwtUtils.parseJwt(accessor);
//            if (jwt != null && jwtUtils.isTokenValid(jwt)) {
//                String username = jwtUtils.extractUsername(jwt);
//
//                UserDetails userDetails = userRepository.findByUsername(username);
//                UsernamePasswordAuthenticationToken authentication =
//                        new UsernamePasswordAuthenticationToken(
//                                userDetails, null, userDetails.getAuthorities());
//                accessor.setUser(authentication);
//            }
//        }
//        return message;
//    }
//}
