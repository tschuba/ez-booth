/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.ui.util;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.router.RouteParameters;
import com.vaadin.flow.router.RouterLink;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import tschuba.ez.booth.model.DataModel;

public class Routing {

    public static String urlForView(
            Class<? extends Component> targetView, RouteParameters parameters) {
        RouterLink routerLink =
                Optional.ofNullable(parameters)
                        .map(params -> new RouterLink(targetView, params))
                        .orElseGet(() -> new RouterLink(targetView));
        return routerLink.getHref();
    }

    public static String urlForView(Class<? extends Component> targetView) {
        return urlForView(targetView, null);
    }

    public static class Parameters {
        public static final String ROUTE_PARAM__BOOTH_ID = "eventId";
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

            public Optional<String> boothId() {
                return parameters.get(ROUTE_PARAM__BOOTH_ID);
            }

            public Optional<DataModel.Booth.Key> boothKey() {
                return boothId().map(id -> DataModel.Booth.Key.builder().boothId(id).build());
            }

            public Optional<String> vendorId() {
                return parameters.get(ROUTE_PARAM__VENDOR_ID);
            }

            public Optional<DataModel.Vendor.Key> vendorKey() {
                Optional<DataModel.Booth.Key> boothKey = boothKey();
                Optional<String> vendorId = vendorId();
                if (boothKey.isPresent() && vendorId.isPresent()) {
                    DataModel.Vendor.Key vendorKey =
                            DataModel.Vendor.Key.builder()
                                    .booth(boothKey.get())
                                    .vendorId(vendorId.get())
                                    .build();
                    return Optional.of(vendorKey);
                }
                return Optional.empty();
            }

            public Optional<String> purchaseId() {
                return parameters.get(ROUTE_PARAM__PURCHASE_ID);
            }

            public Optional<DataModel.Purchase.Key> purchaseKey() {
                Optional<DataModel.Booth.Key> boothKey = boothKey();
                Optional<String> purchaseId = purchaseId();
                if (boothKey.isPresent() && purchaseId.isPresent()) {
                    DataModel.Purchase.Key purchaseKey =
                            DataModel.Purchase.Key.builder()
                                    .booth(boothKey.get())
                                    .purchaseId(purchaseId.get())
                                    .build();
                    return Optional.of(purchaseKey);
                }
                return Optional.empty();
            }

            @SuppressWarnings("unchecked")
            public Optional<Class<? extends Component>> returnToView() {
                return parameters
                        .get(ROUTE_PARAM__RETURN_TO_VIEW)
                        .map(
                                view -> {
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

            public Builder booth(DataModel.Booth.Key key) {
                return boothId(key.boothId());
            }

            public Builder boothId(String boothId) {
                return param(ROUTE_PARAM__BOOTH_ID, boothId);
            }

            public Builder vendor(DataModel.Vendor.Key vendor) {
                return booth(vendor.booth()).vendorId(vendor.vendorId());
            }

            public Builder vendorId(String vendorId) {
                return param(ROUTE_PARAM__VENDOR_ID, vendorId);
            }

            public Builder purchase(DataModel.Purchase.Key purchase) {
                return booth(purchase.booth()).purchaseId(purchase.purchaseId());
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
}
