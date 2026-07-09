package com.sang.sourcepattern.service;

import com.sang.sourcepattern.entity.User;

public interface TierUpgradeService {
    /**
     * @param spendingDelta số tiền cần CỘNG THÊM vào total_spending hiện có của user trước khi
     *                      xét lên hạng (ví dụ: giá trị booking vừa COMPLETED). Truyền 0 khi chỉ
     *                      cần xét hạng khởi điểm mà không có khoản chi tiêu mới nào (vd: user mới tạo).
     */
    void processTierUpgrade(User user, double spendingDelta);
}
