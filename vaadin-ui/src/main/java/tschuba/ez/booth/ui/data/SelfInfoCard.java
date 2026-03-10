/**
 * Copyright (c) 2026 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.ui.data;

import static tschuba.ez.booth.i18n.TranslationKeys.DataExchangeView.SelfInfo.ADDRESS_LABEL__TEXT;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.card.Card;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import com.vaadin.flow.theme.lumo.LumoUtility;
import lombok.NonNull;
import org.springframework.core.env.Environment;
import org.vaadin.barcodes.Barcode;
import tschuba.ez.booth.i18n.TranslationKeys;
import tschuba.ez.booth.ui.util.AddressCodec;
import tschuba.ez.booth.ui.util.Badges;
import tschuba.ez.booth.ui.util.Server;
import tschuba.ez.booth.ui.util.Spacing;

/**
 * Card component for displaying the user's own booth information, such as the external gRPC address and a QR code for it.
 */
@SpringComponent
@UIScope
public class SelfInfoCard extends Composite<Card> {

  private final Environment environment;

  private final VerticalLayout contentLayout = new VerticalLayout();
  private final Span address = new Span();
  private final NativeLabel addressLabel = new NativeLabel();
  private final Span shortAddress = new Span();
  private final NativeLabel shortAddressLabel = new NativeLabel();
  private Barcode qrCode;

  public SelfInfoCard(@NonNull Environment environment) {
    this.environment = environment;

    Badges.highlight(address);
    Badges.highlight(shortAddress);

    HorizontalLayout addressLayout =
        new HorizontalLayout(FlexComponent.Alignment.CENTER, addressLabel, address);
    addressLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
    addressLayout.addClassNames(LumoUtility.Width.FULL);

    HorizontalLayout shortAddressLayout =
        new HorizontalLayout(FlexComponent.Alignment.CENTER, shortAddressLabel, shortAddress);
    shortAddressLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
    shortAddressLayout.addClassNames(LumoUtility.Width.FULL);

    contentLayout.setAlignItems(FlexComponent.Alignment.CENTER);
    contentLayout.add(addressLayout, shortAddressLayout);
    Spacing.spacing(contentLayout).small();
    getContent().add(contentLayout);
  }

  @Override
  protected void onAttach(AttachEvent event) {
    String externalGrpcAddress = Server.externalGrpcAddress(environment);
    String shortExternalGrpcAddress = AddressCodec.Exchange.encode(externalGrpcAddress);

    getContent().setTitle(getTranslation(TranslationKeys.DataExchangeView.SelfInfo.TITLE));

    address.setText(externalGrpcAddress);
    addressLabel.setText(getTranslation(ADDRESS_LABEL__TEXT));
    shortAddress.setText(shortExternalGrpcAddress);
    shortAddressLabel.setText(getTranslation(TranslationKeys.Common.OR));

    if (qrCode != null) {
      contentLayout.remove(qrCode);
    }
    qrCode = new Barcode(externalGrpcAddress, Barcode.Type.qrcode);
    contentLayout.add(qrCode);
  }
}
