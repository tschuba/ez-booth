/* Licensed under MIT

Copyright (c) 2025 Thomas Schulte-Bahrenberg */
package tschuba.ez.booth.ui.util;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.router.RouteParameters;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import tschuba.basarix.data.model.EventKey;
import tschuba.basarix.data.model.PurchaseKey;
import tschuba.basarix.data.model.VendorKey;

public class RoutingParameters {
    public static final String ROUTE_PARAM__EVENT_ID = "eventId";
    public static final String ROUTE_PARAM__VENDOR_ID = "vendorId";
    public static final String ROUTE_PARAM__PURCHASE_ID = "purchaseId";
    public static final String ROUTE_PARAM__RETURN_TO_VIEW = "returnToView";

    public static Parser parser(RouteParameters parameters) {
        return new Parser(parameters);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Parser {
        private final RouteParameters parameters;

        private Parser(RouteParameters parameters) {
            this.parameters = parameters;
        }

        public Optional<String> eventId() {
            return parameters.get(ROUTE_PARAM__EVENT_ID);
        }

        public Optional<EventKey> eventKey() {
            return eventId().map(EventKey::of);
        }

        public Optional<Integer> vendorId() {
            return parameters.get(ROUTE_PARAM__VENDOR_ID).map(Integer::parseInt);
        }

        public Optional<VendorKey> vendorKey() {
            Optional<EventKey> eventKey = eventKey();
            Optional<Integer> vendorId = vendorId();
            if (eventKey.isPresent() && vendorId.isPresent()) {
                return Optional.of(VendorKey.of(eventKey.get(), vendorId.get()));
            }
            return Optional.empty();
        }

        public Optional<String> purchaseId() {
            return parameters.get(ROUTE_PARAM__PURCHASE_ID);
        }

        public Optional<PurchaseKey> purchaseKey() {
            Optional<EventKey> eventKey = eventKey();
            Optional<String> purchaseId = purchaseId();
            if (eventKey.isPresent() && purchaseId.isPresent()) {
                return Optional.of(PurchaseKey.of(eventKey.get(), purchaseId.get()));
            }
            return Optional.empty();
        }

        @SuppressWarnings("unchecked")
        public Optional<Class<? extends Component>> returnToView() {
            return parameters.get(ROUTE_PARAM__RETURN_TO_VIEW).map(view -> {
                try {
                    return (Class<? extends Component>) Class.forName(view);
                } catch (ClassNotFoundException ex) {
                    throw new RuntimeException(ex);
                }
            });
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Builder {
        private final Map<String, String> params = new LinkedHashMap<>();

        public Builder event(EventKey key) {
            return eventId(key.getId());
        }

        public Builder eventId(String eventId) {
            return param(ROUTE_PARAM__EVENT_ID, eventId);
        }

        public Builder vendor(VendorKey vendor) {
            return event(vendor.getEvent()).vendorId(vendor.getId());
        }

        public Builder vendorId(Integer vendorId) {
            return param(ROUTE_PARAM__VENDOR_ID, Integer.toString(vendorId));
        }

        public Builder purchase(PurchaseKey purchase) {
            return event(purchase.getEvent()).purchaseId(purchase.getId());
        }

        public Builder purchaseId(String purchaseId) {
            return param(ROUTE_PARAM__PURCHASE_ID, purchaseId);
        }

        public Builder returnToView(Class<? extends Component> view) {
            return param(ROUTE_PARAM__RETURN_TO_VIEW, view.getName());
        }

        Builder param(String name, String value) {
            params.put(name, value);
            return this;
        }

        public RouteParameters build() {
            return new RouteParameters(params);
        }
    }
}
