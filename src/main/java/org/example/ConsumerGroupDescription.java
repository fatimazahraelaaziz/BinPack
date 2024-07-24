package org.example;

import java.util.ArrayList;
import java.util.List;

public class ConsumerGroupDescription {
    private List<MemberDescription> members;

    public ConsumerGroupDescription() {
        this.members = new ArrayList<>();
    }

    public List<MemberDescription> members() {
        return members;
    }

    public void addMember(MemberDescription member) {
        this.members.add(member);
    }
}

class MemberDescription {
    private String memberId;

    public MemberDescription(String memberId) {
        this.memberId = memberId;
    }

    public String memberId() {
        return memberId;
    }
}
