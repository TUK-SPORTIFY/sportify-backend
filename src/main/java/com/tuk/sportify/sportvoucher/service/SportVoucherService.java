package com.tuk.sportify.sportvoucher.service;

import com.tuk.sportify.crew.domain.Crew;
import com.tuk.sportify.crew.service.CrewService;
import com.tuk.sportify.crewvoucher.service.CrewVoucherProxyService;
import com.tuk.sportify.global.status_code.ErrorCode;
import com.tuk.sportify.global.utils.SportifyDateFormatter;
import com.tuk.sportify.sportvoucher.domain.SportVoucher;
import com.tuk.sportify.sportvoucher.dto.PopularVoucherResponse;
import com.tuk.sportify.sportvoucher.dto.VoucherDetailResponse;
import com.tuk.sportify.sportvoucher.dto.VoucherResponse;
import com.tuk.sportify.sportvoucher.dto.VoucherSearchResponse;
import com.tuk.sportify.sportvoucher.exception.SportVoucherNotFoundException;
import com.tuk.sportify.sportvoucher.repository.SportVoucherRepository;

import com.tuk.sportify.sportvoucher.service.mapper.SportVoucherMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class SportVoucherService {

    private final SportVoucherRepository sportVoucherRepository;
    private final SportVoucherMapper sportVoucherMapper;
    private final CrewVoucherProxyService crewVoucherProxyService;

    public PopularVoucherResponse findPopularVoucher(
            final String city,
            final String gu,
            final Integer fetchSize) {
        Integer currentDate = SportifyDateFormatter.getCurrentDate();
        return new PopularVoucherResponse(
            findPopularVouchers(city, gu, fetchSize, currentDate)
        );
    }

    private List<VoucherResponse> findPopularVouchers(final String city, final String gu,
        final Integer fetchSize, final Integer currentDate) {
        List<SportVoucher> sportVouchers = sportVoucherRepository.findPopularVoucherByCityAndGu(
            city, gu, currentDate, Limit.of(fetchSize));
        return sportVoucherMapper.toVouchersResponse(sportVouchers);
    }

    public VoucherDetailResponse getSportVoucher(final Long sportVoucherId){
        final SportVoucher sportVoucher = getSportVoucherById(sportVoucherId);
        final List<Crew> crews = crewVoucherProxyService.findCrews(sportVoucher);
        final Integer currentDate = SportifyDateFormatter.getCurrentDate();
        sportVoucher.validateOpening(currentDate);
        return sportVoucherMapper.toVoucherDetailResponse(sportVoucher,crews);
    }

    public SportVoucher getSportVoucherById(final Long sportVoucherId) {
        return sportVoucherRepository
            .findById(sportVoucherId)
            .orElseThrow(() -> new SportVoucherNotFoundException(ErrorCode.SPORT_VOUCHER_NOT_FOUND));
    }

    public VoucherSearchResponse searchVoucherByMiddleCategory(final String city, final String gu,
        final Long middleCategoryId){
        final Integer currentDate = SportifyDateFormatter.getCurrentDate();
        final List<SportVoucher> sportVouchers = sportVoucherRepository.findByMiddleCategoryJoinFetch(city, gu,
            middleCategoryId,currentDate);
        return sportVoucherMapper.toVoucherSearchResponse(sportVouchers);
    }
}
