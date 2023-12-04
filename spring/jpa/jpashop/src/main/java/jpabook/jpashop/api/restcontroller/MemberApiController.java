package jpabook.jpashop.api.restcontroller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    @GetMapping("/api/v1/members")
    public List<Member> membersV1() { // ! api는 반환값으로 엔티티 X (아래와 길게 적힌 이유와 동일한 이유)
        return memberService.findMembers();
    }

    @GetMapping("/api/v2/members")
    public Result membersV2() {
        List<Member> findMembers = memberService.findMembers();
        List<MemberDto> collect = findMembers.stream()
                .map(m -> new MemberDto(m.getName()))
                .collect(Collectors.toList());

        // ! Member 객체를 MemberDto 객체로 변환하고, 확장성을 위해 Result 객체로 감싸서 반환
        return new Result(collect);
    }

    @Data
    @AllArgsConstructor
    static class Result<T> {
        private T data;
    }

    @Data
    @AllArgsConstructor
    static class MemberDto {
        private String name;
    }

    @PostMapping("/api/v1/members")
    public CreateMemberResponse saveMemberV1(@RequestBody @Validated Member member) {
        Long id = memberService.join(member);

        return new CreateMemberResponse(id);
    }

    // * 사실 위의 함수에서도 Member 엔티티 객체 대신, dto를 통해 데이터를 받아오는게 더 좋은 방법이다.
    // * 엔티티 클래스는 최대한 순수하게 유지하자.
    // ! api에서는 절대 엔티티를 반환하면 안되기 떄문에 api 스펙에 맞는 dto 객체를 만들어 사용한다.
    // ! api 스펙으로 엔티티를 사용하면 엔티티 변경시 api 스펙이 바뀌어버리는 부작용이 발생한다.
    // ! 즉, 클라이언트에서 오는 데이터도, 클라이언트에게 가는 데이터도 dto를 사용해야한다.
    @Data
    static class CreateMemberResponse {
        private Long id;

        public CreateMemberResponse(Long id) {
            this.id = id;
        }
    }

    // * 클라이언트에서 오는 데이터도, 클라이언트에게 가는 데이터도 dto를 사용
    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMemberV2(@RequestBody @Validated CreateMemberRequest request) {

        Member member = new Member();
        member.setName(request.getName());

        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    @Data // * api를 요청하기위해서 어떤 데이터가 넘어오는지 확인하려면 엔티티가 아닌 dto만 확인하면 된다.
    static class CreateMemberRequest {

        @NotEmpty // 검증을 위한 annotation이 dto에 들어가면 깔끔하다. 엔티티가 순수하게 유지될 수 있음.
        private String name;

        public CreateMemberRequest(String name) {
            this.name = name;
        }
    }

    @PutMapping("/api/v2/members/{id}")
    public UpdateMemberResponse updateMemberV2(
            @PathVariable Long id,
            @RequestBody @Valid UpdateMemberRequest request) {

        memberService.update(id, request.getName());
        Member findMember = memberService.findOne(id);

        return new UpdateMemberResponse(findMember.getId(), findMember.getName());
    }

    @Data
    static class UpdateMemberRequest {

        private String name;

    }

    @Data
    @AllArgsConstructor
    static class UpdateMemberResponse {
        private Long id;
        private String name;
    }

}
