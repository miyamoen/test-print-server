package heimdall;

import enkan.Env;
import enkan.collection.OptionMap;
import enkan.config.EnkanSystemFactory;
import enkan.system.EnkanSystem;
import enkan.component.ApplicationComponent;
import enkan.component.jackson.JacksonBeansConverter;
import enkan.component.jetty.JettyComponent;
import static enkan.component.ComponentRelationship.component;
import static enkan.util.BeanBuilder.builder;

public class MySystemFactory implements EnkanSystemFactory {
    @Override
    public EnkanSystem create() {
        return EnkanSystem.of(
                "jackson", new JacksonBeansConverter(),
                "app", new ApplicationComponent(MyApplicationFactory.class.getName()),
                "http", builder(new JettyComponent())
                        .set(JettyComponent::setPort, Env.getInt("PORT", 3000))
                        .build()
        ).relationships(
                component("http").using("app"),
                component("app").using("jackson")
        );

    }

}
