package com.rin.mrlamel.common.controller;

import com.rin.mrlamel.common.dto.HolidaySolarDto;
import com.rin.mrlamel.common.dto.response.ApiRes;
import com.rin.mrlamel.common.utils.HolidayService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/common")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommonController {
    // Đây là nơi bạn có thể thêm các endpoint chung cho ứng dụng của mình
    // Ví dụ: endpoint để lấy thông tin về holidays, hoặc các thông tin khác mà không thuộc về một module cụ thể nào cả

    // Hiện tại, chúng ta chỉ định nghĩa class này để có thể mở rộng sau này
    // Nếu bạn cần thêm các endpoint cụ thể, hãy tạo các phương thức tương ứng ở đây


    HolidayService holidayService;
    @GetMapping("/holidays/solar")
    public ApiRes<List<HolidaySolarDto>> getHolidays(
            @RequestParam(required = false) List<Integer> years
    ) {
        // Nếu frontend không gửi gì, dùng năm hiện tại làm mặc định
        if (years == null || years.isEmpty()) {
            years = List.of(java.time.LocalDate.now().getYear());
        }

        // Lấy danh sách holiday gộp theo các năm, đã xử lý trùng ngày
        List<HolidaySolarDto> holidays = holidayService.getHolidayDtosForYears(years);

        return ApiRes.success(holidays);
    }

}
