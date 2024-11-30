package com.tuk.sportify.sportvoucher.service;

import com.tuk.sportify.global.status_code.ErrorCode;
import com.tuk.sportify.member.domain.Member;
import com.tuk.sportify.member.service.MemberService;
import com.tuk.sportify.sportvoucher.domain.SportVoucher;
import com.tuk.sportify.sportvoucher.dto.PopularSportResponse;
import com.tuk.sportify.sportvoucher.exception.PopularSportNotFoundException;
import com.tuk.sportify.sportvoucher.repository.PopularSportRepository;
import com.tuk.sportify.sportvoucher.service.mapper.PopularSportMapper;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PopularSportService {

    private final PopularSportRepository popularSportRepository;
    private final MemberService memberService;
    private final PopularSportMapper popularSportMapper;

    public List<PopularSportResponse> findPopularSports(Long memberId) {

        final Member member = memberService.getMemberById(memberId);
        final Point locationPoint = member.getAddress().getPoint();
        final int currentDate = SportVoucherConst.CURRENT_DATE.getValue();
        final int radius = SportVoucherConst.POPULAR_VOUCHER_SEARCH_RADIUS.getValue();

        List<SportVoucher> popularVouchers = popularSportRepository.findPopularSports(
                locationPoint, radius, currentDate, member.isDisabled());

        if (popularVouchers.isEmpty()) {
            throw new PopularSportNotFoundException(ErrorCode.POPULAR_SPORT_NOT_FOUND);
        }

        // 종목별로 신청자 수를 그룹화하고 총합 계산
        Map<String, Integer> sportNameToCount = popularVouchers.stream()
                .collect(Collectors.groupingBy(
                        voucher -> voucher.getMiddleCategory().getName(),
                        Collectors.summingInt(voucher -> voucher.getCourse().getRequestNumberOfPerson())
                ));

        // 상위 3개 종목 반환
        return popularSportMapper.toPopularSportResponses(
                sportNameToCount.entrySet().stream()
                        .sorted((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()))
                        .limit(3)
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue
                        ))
        );
    }
}