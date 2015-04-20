package com.llr.chihuo.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;
import org.springframework.webflow.config.AbstractFlowConfiguration;
import org.springframework.webflow.definition.registry.FlowDefinitionRegistry;
import org.springframework.webflow.engine.builder.support.FlowBuilderServices;
import org.springframework.webflow.executor.FlowExecutor;
import org.springframework.webflow.mvc.builder.MvcViewFactoryCreator;
import org.springframework.webflow.mvc.servlet.FlowController;

@Configuration
public class WebFlowConfig extends AbstractFlowConfiguration {
	@Bean
    public FlowExecutor flowExecutor() {
        return getFlowExecutorBuilder(flowRegistry())
                .build();
    }
	
	@Bean
    public FlowDefinitionRegistry flowRegistry() {
        return getFlowDefinitionRegistryBuilder(flowBuilderServices())
                .setBasePath("classpath:/webflow")
                .addFlowLocationPattern("/*.xml")
                .build();
    }
     
    @Bean
    public FlowBuilderServices flowBuilderServices() {
        return getFlowBuilderServicesBuilder()
                .setViewFactoryCreator(mvcViewFactoryCreator())
                .setValidator(new LocalValidatorFactoryBean())
                .setDevelopmentMode(true)
                .build();
    }
    
    @Bean
    public ViewResolver viewResolver(){
    	InternalResourceViewResolver view = new InternalResourceViewResolver();
        view.setViewClass(JstlView.class);
        view.setPrefix("/WEB-INF/views/");
        view.setSuffix(".jsp");
        return view;
    }
    
    @Bean
    public MvcViewFactoryCreator mvcViewFactoryCreator() {
        MvcViewFactoryCreator mvcViewFactoryCreator = new MvcViewFactoryCreator();
        mvcViewFactoryCreator.setViewResolvers(Arrays.<ViewResolver>asList(viewResolver()));
        return mvcViewFactoryCreator;
    }
    
    @Bean(name = { "/shopping", "/shopping/*" })
    public FlowController flowController(){
    	FlowController flowController = new FlowController();
    	flowController.setFlowExecutor(flowExecutor());
		return flowController;
    }
}
