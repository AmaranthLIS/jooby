package apps;

import io.jooby.App;
import io.jooby.DefaultHeaders;
import io.jooby.Mode;
import io.jooby.jackson.Jackson;
import io.jooby.jetty.Jetty;
import io.jooby.netty.Netty;

public class HelloApp extends App {

  private static final String MESSAGE = "Hello World!";

  static class Message {
    public final String message;

    public Message(String message) {
      this.message = message;
    }
  }

  {
    defaultContentType("text/plain");

    filter(next -> ctx -> {
      System.out.println(Thread.currentThread());
      return next.apply(ctx);
    });

    filter(new DefaultHeaders());

    get("/", ctx -> ctx.sendText(MESSAGE));

    get("/{foo}", ctx -> ctx.sendText("Hello World!"));

    dispatch(() -> {
      filter(next -> ctx -> {
        System.out.println(Thread.currentThread());
        return next.apply(ctx);
      });
      get("/worker", ctx -> ctx.sendText("Hello Worker!"));
    });

    renderer(new Jackson());
    get("/json", ctx -> ctx.type("application/json").send(new Message("Hello World!")));

    error((ctx, cause, statusCode) -> {
      ctx.statusCode(statusCode)
          .sendText(statusCode.reason());
    });
  }

  public static void main(String[] args) {
    new Netty()
        .deploy(new HelloApp().mode(Mode.WORKER))
        .start()
        .join();
  }
}