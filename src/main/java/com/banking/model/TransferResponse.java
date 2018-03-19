package com.banking.model;

import java.util.Date;

/**
 * Entity contains operation result
 */
public class TransferResponse {

    private Status status;

    private Date date;

    private TransferResponse() {
    }

    public TransferResponse(Status status) {
        this.status = status;
        this.date = new Date();
    }

    public Status getStatus() {
        return status;
    }

    public Date getDate() {
        return date;
    }

    public enum Status {
        SUCCESS(1000, "Success"),
        ACCOUNT_DOES_NOT_EXIST(4001, "Account doesn't exist"),
        TARGET_ACCOUNT_DOES_NOT_EXIST(4002, "Target account doesn't exist"),
        INSUFFICIENT_FUNDS(4003, "Insufficient funds"),
        TARGET_ACCOUNT_IS_THE_SAME(4004, "Target account must be another account"),
        INVALID_REQUEST(4006, "Invalid request"),
        INTERNAL_ERROR(5000, "Request hasn't processed");

        private final int code;
        private final String reason;

        Status(int statusCode, String reason) {
            this.code = statusCode;
            this.reason = reason;
        }

        public int getCode() {
            return code;
        }

        public String getReason() {
            return reason;
        }
    }

    @Override
    public String toString() {
        return "TransferResponse{" +
                "status=" + status +
                ", date=" + date +
                '}';
    }
}
