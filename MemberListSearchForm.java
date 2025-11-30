package com.example.large.membersystem.controller.member;

import java.time.LocalDate;

public class MemberListSearchForm {
    public String keyword;
    public String status;
    public LocalDate registeredFrom;
    public LocalDate registeredTo;
    public boolean showOnlyRisky;
}
