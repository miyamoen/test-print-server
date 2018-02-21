package heimdall;

import enkan.Application;
import enkan.application.WebApplication;
import enkan.config.ApplicationFactory;
import enkan.endpoint.ResourceEndpoint;
import enkan.predicate.NonePredicate;
import enkan.middleware.*;
import enkan.middleware.devel.*;
import kotowari.middleware.*;
import kotowari.middleware.serdes.ToStringBodyWriter;
import enkan.system.inject.ComponentInjector;
import kotowari.routing.Routes;
import heimdall.controller.IndexController;

import static enkan.util.BeanBuilder.builder;
import static enkan.util.Predicates.*;

public class MyApplicationFactory implements ApplicationFactory {
    @Override
    public Application create(ComponentInjector injector) {
        WebApplication app = new WebApplication();

        Routes routes = Routes.define(r -> {
            r.get("/").to(IndexController.class, "index");
            r.post("/print").to(IndexController.class, "print");
        }).compile();

        app.use(new DefaultCharsetMiddleware<>());
        app.use(none(), new ServiceUnavailableMiddleware<>(new ResourceEndpoint("/public/html/503.html")));
        app.use(envIn("development"), new LazyLoadMiddleware<>("enkan.middleware.devel.StacktraceMiddleware"));
        app.use(envIn("development"), new LazyLoadMiddleware<>("enkan.middleware.devel.TraceWebMiddleware"));
        app.use(new TraceMiddleware<>());
        app.use(new ContentTypeMiddleware<>());
        app.use(envIn("development"), new LazyLoadMiddleware<>("enkan.middleware.devel.HttpStatusCatMiddleware"));
        app.use(new ParamsMiddleware<>());
        app.use(new MultipartParamsMiddleware<>());
        app.use(new MethodOverrideMiddleware<>());
        app.use(new NormalizationMiddleware<>());
        app.use(new NestedParamsMiddleware<>());
        app.use(new CookiesMiddleware<>());
        app.use(new SessionMiddleware<>());
        app.use(new ContentNegotiationMiddleware<>());

        app.use(new ResourceMiddleware<>());
        app.use(new RenderTemplateMiddleware<>());
        app.use(new RoutingMiddleware<>(routes));
        app.use(new FormMiddleware<>());
        app.use(builder(new SerDesMiddleware<>())
                .set(SerDesMiddleware::setBodyWriters, new ToStringBodyWriter())
                .build());
        app.use(new ValidateBodyMiddleware<>());
        app.use(new ControllerInvokerMiddleware<>(injector));

        return app;
    }
}
