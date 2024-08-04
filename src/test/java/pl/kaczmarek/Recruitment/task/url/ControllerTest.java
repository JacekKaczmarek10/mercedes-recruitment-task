package pl.kaczmarek.Recruitment.task.url;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.web.util.UriTemplate;

public abstract class ControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @SneakyThrows
    protected <T> String toJsonString(final T object) {
        return objectMapper.writeValueAsString(object);
    }

    protected ResultMatcher isEqualToJsonOf(final Object o) {
        return result -> assertThat(result.getResponse().getContentAsString()).isEqualTo(toJsonString(o));
    }

    protected URI buildUri(final String endpointUriTemplate, final Object... uriVariableValues) {
        return new UriTemplate(StringUtils.prependIfMissing(endpointUriTemplate, "/")).expand(uriVariableValues);
    }
}
