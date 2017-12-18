package io.jooby.internal.netty;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.kqueue.KQueue;
import io.netty.channel.kqueue.KQueueEventLoopGroup;
import io.netty.channel.kqueue.KQueueServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public interface NettyConfigurer {
  EventLoopGroup group(int threads);

  Class<? extends ServerSocketChannel> channel();

  static NettyConfigurer get() {
    /** Epoll: */
    if (Epoll.isAvailable()) {
      return new NettyConfigurer() {
        @Override public EventLoopGroup group(int threads) {
          return new EpollEventLoopGroup(threads);
        }

        @Override public Class<? extends ServerSocketChannel> channel() {
          return EpollServerSocketChannel.class;
        }
      };
    }
    if (KQueue.isAvailable()) {
      return new NettyConfigurer() {
        @Override public EventLoopGroup group(int threads) {
          return new KQueueEventLoopGroup(threads);
        }

        @Override public Class<? extends ServerSocketChannel> channel() {
          return KQueueServerSocketChannel.class;
        }
      };
    }
    return new NettyConfigurer() {
      @Override public EventLoopGroup group(int threads) {
        return new NioEventLoopGroup(threads);
      }

      @Override public Class<? extends ServerSocketChannel> channel() {
        return NioServerSocketChannel.class;
      }
    };
  }
}
