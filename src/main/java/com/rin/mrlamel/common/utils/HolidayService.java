package com.rin.mrlamel.common.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nlf.calendar.Lunar;
import com.nlf.calendar.Solar;
import com.rin.mrlamel.common.dto.HolidayDTO;
import com.rin.mrlamel.common.dto.HolidaySolarDto;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class HolidayService {

    ObjectMapper objectMapper;

    public List<LocalDate> getHolidaysForYear(int year) {
        try {
            List<HolidaySolarDto> holidays = getHolidaySolarForYear(year);


            return holidays.stream()
                    .map(HolidaySolarDto::getDate)
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi load danh sách ngày lễ: " + e.getMessage(), e);
        }
    }
    public List<HolidaySolarDto> getHolidaySolarForYear(int year) {
        try {
            InputStream is = getClass().getResourceAsStream("/holidays.json");
            if (is == null) {
                throw new RuntimeException("Không tìm thấy file holidays.json trong resources");
            }

            List<HolidayDTO> holidays = objectMapper.readValue(is, new TypeReference<>() {});

            List<HolidaySolarDto> result = new ArrayList<>();
            // Lấy ngày Tết Nguyên Đán
            Map<LocalDate, String> tetDays = getLunarTet(year);
            for (Map.Entry<LocalDate, String> entry : tetDays.entrySet()) {
                HolidaySolarDto dto = HolidaySolarDto.builder()
                        .date(entry.getKey())
                        .name(entry.getValue())
                        .year(year)
                        .originDate(entry.getKey().toString())
                        .rootType("lunar")
                        .build();
                result.add(dto);
            }

            for (HolidayDTO h : holidays) {
                String[] parts = h.getDate().split("/");
                int day = Integer.parseInt(parts[0]);
                int month = Integer.parseInt(parts[1]);


                LocalDate solarDate;
                if ("solar".equalsIgnoreCase(h.getType())) {
                    solarDate = LocalDate.of(year, month, day);
                } else {
                    Lunar lunar = Lunar.fromYmd(year, month, day);
                    Solar solar = lunar.getSolar();
                    solarDate = LocalDate.of(solar.getYear(), solar.getMonth(), solar.getDay());
                }

                HolidaySolarDto dto = HolidaySolarDto.builder()
                        .date(solarDate)
                        .name(h.getName())
                        .year(year)
                        .originDate(h.getDate())
                        .rootType(h.getType())
                        .build();

                result.add(dto);
            }

            // Sort theo ngày trong năm
            result.sort(Comparator.comparing(HolidaySolarDto::getDate));
//            result.sort(Comparator.comparing(HolidaySolarDto::getDate).reversed()); // Nếu muốn sắp xếp ngược lại


            return result;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi load danh sách ngày lễ: " + e.getMessage(), e);
        }
    }


    private Map<LocalDate, String> getLunarTet(int solarYear) {
        // Lấy ngày 1/1 âm lịch của năm dương tương ứng
        Lunar tetLunar = Lunar.fromYmd(solarYear, 1, 1);
        Solar tetSolar = tetLunar.getSolar();
        LocalDate tetDate = LocalDate.of(tetSolar.getYear(), tetSolar.getMonth(), tetSolar.getDay());

        Map<LocalDate, String> tetDays = new HashMap<>();

        // 7 ngày trước Tết
        for (int i = 7; i >= 1; i--) {
            tetDays.put(tetDate.minusDays(i), "Nghỉ trước Tết");
        }

        // 4 ngày Tết (1->4 âm lịch)
        for (int i = 0; i < 4; i++) {
            tetDays.put(tetDate.plusDays(i), "Tết Nguyên Đán");
        }

        // 7 ngày sau Tết (5->11 âm lịch)
        for (int i = 4; i <= 10; i++) {
            tetDays.put(tetDate.plusDays(i), "Nghỉ sau Tết");
        }

        return tetDays;
    }




}
