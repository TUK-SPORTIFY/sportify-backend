package com.tuk.sportify.vouchermember.controller;

import com.tuk.sportify.global.argumentresolver.AuthenticationMember;
import com.tuk.sportify.global.response.ApiErrorCodeExample;
import com.tuk.sportify.global.response.ApiErrorCodeExamples;
import com.tuk.sportify.global.status_code.ErrorCode;
import com.tuk.sportify.vouchermember.dto.CrewMembersResponse;
import com.tuk.sportify.vouchermember.dto.MyCurrentCrewResponse;
import com.tuk.sportify.vouchermember.dto.MyPastCrewResponse;
import com.tuk.sportify.vouchermember.dto.PastPersonalVoucherResponse;
import com.tuk.sportify.vouchermember.dto.PersonalAndCrewVoucherResponse;
import com.tuk.sportify.vouchermember.service.VoucherMemberService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/voucher-members")
@Tag(name = "스포츠 이용권 이용자 [현재 참여중인 개인,크루 이용권 | 과거에 참여했던 개인,크루 이용권]")
public class VoucherMemberController {

    private final VoucherMemberService voucherMemberService;

    // 현재 활동중인 이용권 목록 (개인+크루)
    @GetMapping
    @Operation(summary = "현재 사용 중인 이용권 목록", description = "현재 이용중인 개인이용권과 크루이용권을 모두 포함하여 반환합니다.")
    @ApiErrorCodeExample(ErrorCode.MEMBER_NOT_FOUND)
    public PersonalAndCrewVoucherResponse findPersonalAndCrewVouchers(
            @AuthenticationMember @Parameter(hidden = true) final Long memberId) {
        return voucherMemberService.findPersonalAndCrewVouchers(memberId);
    }

    @GetMapping("/my-current-crews")
    @Operation(summary = "현재 참여 중인 크루 목록", description = "현재 참여중인 모든 크루 목록을 반환합니다.")
    @ApiErrorCodeExample(ErrorCode.MEMBER_NOT_FOUND)
    public MyCurrentCrewResponse findMyCurrentCrews(
            @AuthenticationMember @Parameter(hidden = true) final Long memberId) {
        return voucherMemberService.findMyCurrentCrews(memberId);
    }

    @GetMapping("/my-past-crews")
    @Operation(
            summary = "과거에 참여한 크루 목록",
            description = "과거에 참여했던 크루 목록을 페이징 처리하여 반환합니다. 페이징 방식은 넘버링이 아닌 스크롤 방식입니다.")
    @ApiErrorCodeExample(ErrorCode.MEMBER_NOT_FOUND)
    public MyPastCrewResponse findMyPastCrews(
            @AuthenticationMember @Parameter(hidden = true) final Long memberId,
            @RequestParam @Parameter(description = "페이지") Integer page,
            @RequestParam @Parameter(description = "한 번에 가져올 크기") Integer fetchSize) {
        return voucherMemberService.findMyPastCrews(memberId, page, fetchSize);
    }

    @GetMapping("/my-past-voucher")
    @Operation(
            summary = "과거에 사용했던 개인 이용권 목록",
            description = "과거에 사용했던 개인 이용권 목록을 페이징 처리하여 반환합니다. 페이징 방식은 넘버링이 아닌 스크롤 방식입니다.")
    @ApiErrorCodeExample(ErrorCode.MEMBER_NOT_FOUND)
    public PastPersonalVoucherResponse findPastPersonalVouchers(
            @AuthenticationMember @Parameter(hidden = true) final Long memberId,
            @RequestParam @Parameter(description = "페이지") Integer page,
            @RequestParam @Parameter(description = "한 번에 가져올 크기") Integer fetchSize) {
        return voucherMemberService.findPastPersonalVouchers(memberId, page, fetchSize);
    }

    @GetMapping("/{crewId}")
    @Operation(summary = "크루에 속한 크루원들 목록",
        description = "크루에 속한 모든 크루원들을 반환합니다.")
    public CrewMembersResponse findCrewMembers(
        @PathVariable @Parameter(description = "크루 ID") final Long crewId
    ){
        return voucherMemberService.findCrewMembers(crewId);
    }

    @PostMapping("/{sportVoucherId}")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "개인 이용권 신청",
        description = "크루가 아닌 개인으로 이용권을 신청합니다.")
    @ApiErrorCodeExamples({ErrorCode.DUPLICATED_PARTICIPATION})
    public void createPersonalVoucherMember(
        @AuthenticationMember @Parameter(hidden = true) final Long memberId,
        @PathVariable @Parameter(description = "이용권 Id") final Long sportVoucherId){
        voucherMemberService.createPersonalVoucherMember(memberId,sportVoucherId);
    }
}
