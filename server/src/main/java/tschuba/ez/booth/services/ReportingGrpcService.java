package tschuba.ez.booth.services;

import io.grpc.stub.StreamObserver;
import lombok.NonNull;
import org.slf4j.Logger;
import org.springframework.grpc.server.service.GrpcService;
import tschuba.ez.booth.DataModel;
import tschuba.ez.booth.proto.*;

import java.net.URI;

/**
 * gRPC service implementation for reporting.
 * This class handles incoming gRPC requests and delegates the operations to the local service layer.
 */
@GrpcService
public class ReportingGrpcService extends ReportingServiceGrpc.ReportingServiceImplBase {

    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(ReportingGrpcService.class);

    private final ReportingLocalService localService;

    private ReportingGrpcService(@NonNull ReportingLocalService localService) {
        this.localService = localService;
    }

    @Override
    public void createVendorReportData(ProtoModel.VendorKey request, StreamObserver<ProtoServices.VendorReportData> responseObserver) {
        try {
            DataModel.Vendor.Key vendor = ProtoMapper.VENDOR_KEY.messageToObject(request);
            ServiceModel.VendorReportData reportData = localService.createVendorReportData(vendor);

            ProtoServices.VendorReportData reportDataMsg = ProtoMapper.VENDOR_REPORT_DATA.objectToMessage(reportData);
            responseObserver.onNext(reportDataMsg);
            responseObserver.onCompleted();
        } catch (Exception ex) {
            LOGGER.error("Error creating vendor report data for vendor: {}", request, ex);
            responseObserver.onError(ex);
        }
    }

    @Override
    public void generateVendorReport(ProtoServices.VendorReportInput request, StreamObserver<ProtoCore.URI> responseObserver) {
        try {
            DataModel.Vendor.Key[] vendors = request.getVendorList().stream().map(ProtoMapper.VENDOR_KEY::messageToObject).toArray(DataModel.Vendor.Key[]::new);
            URI uri = localService.generateVendorReport(vendors);

            ProtoCore.URI uriMsg = ProtoMapper.URI.objectToMessage(uri);
            responseObserver.onNext(uriMsg);
            responseObserver.onCompleted();
        } catch (Exception ex) {
            LOGGER.error("Error generating vendor report for vendors: {}", request, ex);
            responseObserver.onError(ex);
        }
    }
}
