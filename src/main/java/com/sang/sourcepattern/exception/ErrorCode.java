package com.sang.sourcepattern.exception;


import lombok.Getter;
import org.springframework.http.HttpStatus;


@Getter
public enum     ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999,"Lỗi không xác định", HttpStatus.INTERNAL_SERVER_ERROR),
    USER_EXISTED(1001, "Tài khoản đã tồn tại", HttpStatus.BAD_REQUEST),
    ROLE_NOT_FOUND(1002, "Không tìm thấy vai trò", HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(1003, "Chưa xác thực", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1004, "Không có quyền truy cập", HttpStatus.FORBIDDEN),
    INVALID_KEY(1005, "Khóa không hợp lệ", HttpStatus.BAD_REQUEST),
    ACCOUNT_LOCKED(1014, "Tài khoản của bạn đã bị khóa 60 giây do đăng nhập sai nhiều lần", HttpStatus.FORBIDDEN),
    USER_NOT_EXISTED(10010, "Email không tồn tại",HttpStatus.NOT_FOUND),
//   Create user errors
    EMAIL_INVALID(1006, "Email không hợp lệ", HttpStatus.BAD_REQUEST),
    EMAIL_REQUIRED(1009, "Email không được để trống", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(1007, "Mật khẩu không hợp lệ", HttpStatus.BAD_REQUEST),
    PASSWORD_REQUIRED(1008, "Mật khẩu không được để trống", HttpStatus.BAD_REQUEST),

//   Pet errors
    PET_NOT_EXISTED(2001, "Thú cưng không tồn tại", HttpStatus.NOT_FOUND),
    CARE_LOG_NOT_FOUND(2002, "Không tìm thấy nhật ký chăm sóc", HttpStatus.NOT_FOUND),
    NO_IMAGE_IN_CARE_LOG(2003, "Nhật ký chăm sóc này không chứa hình ảnh", HttpStatus.BAD_REQUEST),
    CARE_LOG_NOT_BELONG_TO_PET(2004, "Nhật ký chăm sóc không thuộc về thú cưng này", HttpStatus.BAD_REQUEST),
    OLD_PASSWORD_INVALID(1011, "Mật khẩu cũ không chính xác", HttpStatus.BAD_REQUEST),
    WRONG_PASSWORD(1012, "Mật khẩu không chính xác", HttpStatus.UNAUTHORIZED),
    SHOP_EXISTED(3001, "Cửa hàng đã tồn tại", HttpStatus.BAD_REQUEST),
    SHOP_NOT_FOUND(3002, "Không tìm thấy cửa hàng", HttpStatus.NOT_FOUND),
    ACCOUNT_NOT_VERIFIED(1013, "Tài khoản đang chờ duyệt. Vui lòng đợi quản trị viên xác minh.", HttpStatus.FORBIDDEN),
    ACCOUNT_DEACTIVATED(1015, "Tài khoản đã bị vô hiệu hóa", HttpStatus.FORBIDDEN),
    SHOP_NAME_REQUIRED(3003, "Tên cửa hàng không được để trống", HttpStatus.BAD_REQUEST),
    SHOP_TYPE_REQUIRED(3004, "Loại cửa hàng không được để trống", HttpStatus.BAD_REQUEST),
    INVALID_EMAIL(1014, "Định dạng email không hợp lệ", HttpStatus.BAD_REQUEST),
    PHONE_REQUIRED(3005, "Số điện thoại không được để trống", HttpStatus.BAD_REQUEST),
    INVALID_PHONE(3010, "Số điện thoại không hợp lệ", HttpStatus.BAD_REQUEST),
    ADDRESS_REQUIRED(3006, "Địa chỉ không được để trống", HttpStatus.BAD_REQUEST),
    CITY_REQUIRED(3007, "Thành phố không được để trống", HttpStatus.BAD_REQUEST),
    DESCRIPTION_TOO_SHORT(3008, "Mô tả phải có ít nhất 10 ký tự", HttpStatus.BAD_REQUEST),

//   Service errors
    SERVICE_NOT_FOUND(4001, "Không tìm thấy dịch vụ", HttpStatus.NOT_FOUND),
    SERVICE_NAME_REQUIRED(4002, "Tên dịch vụ không được để trống", HttpStatus.BAD_REQUEST),
    SERVICE_CATEGORY_REQUIRED(4003, "Danh mục dịch vụ không được để trống", HttpStatus.BAD_REQUEST),
    SERVICE_PRICE_REQUIRED(4004, "Giá dịch vụ không được để trống", HttpStatus.BAD_REQUEST),
    SERVICE_PRICE_INVALID(4005, "Giá dịch vụ phải lớn hơn 0", HttpStatus.BAD_REQUEST),
    SERVICE_DURATION_INVALID(4006, "Thời lượng dịch vụ phải ít nhất 1 phút", HttpStatus.BAD_REQUEST),
    SERVICE_DESCRIPTION_TOO_SHORT(4007, "Mô tả dịch vụ phải có ít nhất 10 ký tự", HttpStatus.BAD_REQUEST),
    SERVICE_NOT_BELONG_TO_SHOP(4008, "Dịch vụ không thuộc về cửa hàng của bạn", HttpStatus.FORBIDDEN),
    SHOP_NOT_VERIFIED(3009, "Cửa hàng chưa được xác minh", HttpStatus.FORBIDDEN),

//   Booking errors
    BOOKING_NOT_FOUND(5001, "Không tìm thấy lịch đặt", HttpStatus.NOT_FOUND),
    BOOKING_NOT_BELONG_TO_USER(5002, "Lịch đặt không thuộc về bạn", HttpStatus.FORBIDDEN),
    BOOKING_ALREADY_PAID(5003, "Lịch đặt đã được thanh toán", HttpStatus.BAD_REQUEST),
    BOOKING_CANCELLED(5004, "Lịch đặt đã bị hủy", HttpStatus.BAD_REQUEST),
    PET_NOT_BELONG_TO_USER(5005, "Thú cưng không thuộc về bạn", HttpStatus.FORBIDDEN),
    STAFF_NOT_BELONG_TO_SHOP(5006, "Nhân viên không thuộc về cửa hàng này", HttpStatus.BAD_REQUEST),
    PAYOS_ERROR(5007, "Lỗi cổng thanh toán", HttpStatus.INTERNAL_SERVER_ERROR),
    SHOP_ID_REQUIRED(5008, "Mã cửa hàng không được để trống", HttpStatus.BAD_REQUEST),
    SERVICE_ID_REQUIRED(5009, "Mã dịch vụ không được để trống", HttpStatus.BAD_REQUEST),
    PET_ID_REQUIRED(5010, "Mã thú cưng không được để trống", HttpStatus.BAD_REQUEST),
    APPOINTMENT_DATETIME_REQUIRED(5011, "Ngày giờ hẹn không được để trống", HttpStatus.BAD_REQUEST),
    APPOINTMENT_MUST_BE_FUTURE(5012, "Thời gian hẹn phải ở trong tương lai", HttpStatus.BAD_REQUEST),
    PAYMENT_PENDING(5014, "Thanh toán chưa hoàn tất, vui lòng tiếp tục thanh toán trên màn hình quét mã PayOS.", HttpStatus.BAD_REQUEST),

//   Email verification errors
    EMAIL_NOT_VERIFIED(6001, "Email chưa được xác minh. Vui lòng kiểm tra hộp thư của bạn.", HttpStatus.FORBIDDEN),
    INVALID_VERIFICATION_TOKEN(6002, "Mã xác minh không hợp lệ hoặc đã hết hạn", HttpStatus.BAD_REQUEST),
    EMAIL_ALREADY_VERIFIED(6003, "Email đã được xác minh", HttpStatus.BAD_REQUEST),

//   Password reset errors
    INVALID_RESET_TOKEN(6004, "Mã đặt lại mật khẩu không hợp lệ hoặc đã hết hạn", HttpStatus.BAD_REQUEST),
    RESET_TOKEN_USED(6005, "Mã đặt lại mật khẩu đã được sử dụng", HttpStatus.BAD_REQUEST),

//   Staff management errors
    STAFF_NOT_FOUND(7001, "Không tìm thấy nhân viên", HttpStatus.NOT_FOUND),
    STAFF_ALREADY_INACTIVE(7002, "Tài khoản nhân viên đã không hoạt động", HttpStatus.BAD_REQUEST),
    STAFF_EMAIL_EXISTED(7003, "Email đã được sử dụng bởi tài khoản khác", HttpStatus.BAD_REQUEST),
    BOOKING_NOT_BELONG_TO_STAFF_SHOP(7004, "Lịch đặt không thuộc về cửa hàng của bạn", HttpStatus.FORBIDDEN),
    BOOKING_ALREADY_ASSIGNED(7005, "Lịch đặt đã được phân công cho một nhân viên", HttpStatus.BAD_REQUEST),
    BOOKING_STATUS_INVALID(7006, "Trạng thái lịch đặt không hợp lệ", HttpStatus.BAD_REQUEST),
    MANUAL_ASSIGNMENT_ONLY(7007, "Cửa hàng này chỉ cho phép chủ cửa hàng phân công thủ công", HttpStatus.FORBIDDEN),


//   Wallet errors
    WALLET_NOT_FOUND(8001, "Không tìm thấy ví", HttpStatus.NOT_FOUND),
    INSUFFICIENT_BALANCE(8002, "Số dư khả dụng không đủ", HttpStatus.BAD_REQUEST),
    WITHDRAWAL_NOT_FOUND(8003, "Không tìm thấy yêu cầu rút tiền", HttpStatus.NOT_FOUND),
    WITHDRAWAL_ALREADY_PROCESSED(8004, "Yêu cầu rút tiền đã được xử lý", HttpStatus.BAD_REQUEST),
    PENDING_WITHDRAWAL_EXISTS(8005, "Bạn đang có yêu cầu rút tiền chờ xử lý", HttpStatus.BAD_REQUEST),
    MIN_WITHDRAWAL_AMOUNT_REQUIRED(8006, "Số tiền rút tối thiểu là 200,000 VNĐ", HttpStatus.BAD_REQUEST),

    // Review errors
    REVIEW_NOT_ALLOWED(9001, "Bạn phải hoàn thành lịch đặt trước khi đánh giá cửa hàng này", HttpStatus.FORBIDDEN),
    REVIEW_ALREADY_EXISTED(9002, "Bạn đã đánh giá cửa hàng này rồi", HttpStatus.BAD_REQUEST),


    //   Booking conflict
    PET_BOOKING_CONFLICT(5013, "Thú cưng này đã có lịch đặt vào thời gian được yêu cầu", HttpStatus.CONFLICT),
    STAFF_BOOKING_CONFLICT(5014, "Nhân viên này đã có lịch đặt vào thời gian được yêu cầu", HttpStatus.CONFLICT),
    NO_STAFF_AVAILABLE(5015, "Không có nhân viên trống trong khoảng thời gian này. Vui lòng chọn thời gian khác.", HttpStatus.CONFLICT),
    MISSING_MEDICAL_RECORD(5016, "Phải điền hồ sơ bệnh án trước khi hoàn thành lịch đặt phòng khám", HttpStatus.BAD_REQUEST),

    // Request errors
    REQUEST_NOT_FOUND(10001, "Không tìm thấy yêu cầu", HttpStatus.NOT_FOUND),
    REQUEST_ALREADY_PROCESSED(10002, "Yêu cầu đã được xử lý", HttpStatus.BAD_REQUEST),
    STAFF_CHANGE_REQUEST_ALREADY_EXISTS(10003, "Đã có yêu cầu thay đổi nhân viên đang chờ xử lý cho lịch đặt này", HttpStatus.BAD_REQUEST),
    CANNOT_UPDATE_STATUS_WHILE_REQUEST_PENDING(10004, "Không thể cập nhật trạng thái khi đang có yêu cầu thay đổi nhân viên", HttpStatus.BAD_REQUEST),
    CANNOT_CHANGE_STAFF_DIRECTLY(10005, "Không thể trực tiếp thay đổi khi nhân viên đã được phân công. Vui lòng sử dụng quy trình yêu cầu.", HttpStatus.BAD_REQUEST),
    INVALID_REQUEST(10006,"Yêu cầu không hợp lệ",HttpStatus.BAD_REQUEST),
    DOCKER_NOT_RUNNING(10007, "Docker daemon không chạy hoặc không khởi động được luồng camera", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_TIME_FORMAT(10008, "Định dạng thời gian không hợp lệ. Vui lòng sử dụng định dạng ISO 8601 (ví dụ: 2024-12-31T23:59:59)", HttpStatus.BAD_REQUEST),

    // No-Show cancellation errors
    NO_SHOW_TOO_EARLY(10009, "Chưa thể hủy vì vắng mặt. Thời gian ân hạn chưa kết thúc.", HttpStatus.BAD_REQUEST),
    INVALID_GRACE_PERIOD(10010, "Thời gian ân hạn đến trễ phải từ 5 đến 30 phút", HttpStatus.BAD_REQUEST),
    VOUCHER_NOT_FOUND(10011, "Không tìm thấy mã giảm giá", HttpStatus.NOT_FOUND)
    ;



    private final int code;
    private final String message;
    private final HttpStatus statusCode;

    ErrorCode(int code, String message, HttpStatus statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }
}
