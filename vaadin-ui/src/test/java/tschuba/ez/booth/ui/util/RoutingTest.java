package tschuba.ez.booth.ui.util;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.router.RouteParameters;
import com.vaadin.flow.router.Router;
import com.vaadin.flow.server.VaadinService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

public class RoutingTest {
    private static final String URL_EXPECTED = "test-route";

    private RouteParameters mockedParameters;
    private MockedStatic<VaadinService> mockedStaticService;
    private MockedStatic<RouteConfiguration> mockedStaticRouteConfig;

    @BeforeEach
    void setUp() {
        mockedParameters = mock(RouteParameters.class);

        Router mockedRouter = mock(Router.class);
        VaadinService mockedService = mock(VaadinService.class);
        when(mockedService.getRouter()).thenReturn(mockedRouter);

        mockedStaticService = mockStatic(VaadinService.class);
        mockedStaticService.when(VaadinService::getCurrent).thenReturn(mockedService);

        RouteConfiguration mockedRouteConfig = mock(RouteConfiguration.class);
        when(mockedRouteConfig.getUrl(eq(RoutingTestView.class), any(RouteParameters.class))).thenReturn(URL_EXPECTED);

        mockedStaticRouteConfig = mockStatic(RouteConfiguration.class);
        mockedStaticRouteConfig.when(() -> RouteConfiguration.forRegistry(any())).thenReturn(mockedRouteConfig);
    }

    @AfterEach
    void tearDown() {
        mockedStaticRouteConfig.close();
        mockedStaticService.close();
    }

    @Test
    void urlForViewWithParameters() {
        String actualUrl = Routing.urlForView(RoutingTestView.class, mockedParameters);
        assertThat(actualUrl).isEqualTo(URL_EXPECTED);
    }

    @Test
    void urlForViewWithoutParameters() {
        String actualUrl = Routing.urlForView(RoutingTestView.class);
        assertThat(actualUrl).isEqualTo(URL_EXPECTED);
    }

    @Route("test-route")
    static class RoutingTestView extends Component {
    }
}