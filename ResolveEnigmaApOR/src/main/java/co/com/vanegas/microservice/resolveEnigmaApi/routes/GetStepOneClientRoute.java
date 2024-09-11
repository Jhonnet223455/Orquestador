package co.com.vanegas.microservice.resolveEnigmaApi.routes;

import co.com.vanegas.microservice.resolveEnigmaApi.models.client.ClientJsonApiBodyResponseSuccess;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;

import static javax.xml.bind.JAXB.unmarshal;

public class GetStepOneClientRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:getStepOneClient")
                .setHeader(Exchange.HTTP_METHOD, constant(" POST"))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .to("freemarker:templates/GetStepOneClientTemplate.ftl")
                .log("Request microservice step one: ${body}")
                .to("http://localhost:8080/getStep")
                .convertBodyTo(String.class)
                .log("Response microservice step one: ${body}")
                .unmarshal().json(JsonLibrary.Jackson, ClientJsonApiBodyResponseSuccess.class)
                .log("Response microservice step one: ${body}")
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        ClientJsonApiBodyResponseSuccess stepOneResponse = (ClientJsonApiBodyResponseSuccess) exchange.getIn().getBody();
                        if (stepOneResponse.getData().get(0).getAnswer().equalsIgnoreCase("1"))
                        {
                            exchange.setProperty("step1", stepOneResponse.getData().get(0).getAnswer());
                            exchange.setProperty("Error", "0000");
                            exchange.setProperty("ErrorDescription", "No error");
                        }
                        else
                        {
                            exchange.setProperty("Error", "0001");
                            exchange.setProperty("ErrorDescription", "Error in step one");
                        }

                }
                })
        .log("Response microservice step one: ${body}");

    }
}
