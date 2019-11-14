package com.appster.models;

/**
 * Created by User on 11/4/2015.
 */
public class PocketHistoryModel {


    /**
     * Id : 0
     * Amount : 0
     * TypeTransaction : 0
     * Message : string
     * Remark : string
     * Timestamp : 0
     * Total : 0
     * Status : 0
     * PaymentType : 0
     * Created : string
     */

    private int Id;
    private int Amount;
    private int TypeTransaction;
    private String Message;
    private String Remark;
    private String Timestamp;
    private Long Total;
    private int Status;
    private int PaymentType;
    private String Created;


    public int getId() {
        return Id;
    }

    public void setId(int Id) {
        this.Id = Id;
    }

    public int getAmount() {
        return Amount;
    }

    public void setAmount(int Amount) {
        this.Amount = Amount;
    }

    public int getTypeTransaction() {
        return TypeTransaction;
    }

    public void setTypeTransaction(int TypeTransaction) {
        this.TypeTransaction = TypeTransaction;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String Message) {
        this.Message = Message;
    }

    public String getRemark() {
        return Remark;
    }

    public void setRemark(String Remark) {
        this.Remark = Remark;
    }

    public String getTimestamp() {
        return Timestamp;
    }

    public void setTimestamp(String Timestamp) {
        this.Timestamp = Timestamp;
    }

    public Long getTotal() {
        return Total;
    }

    public void setTotal(Long Total) {
        this.Total = Total;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int Status) {
        this.Status = Status;
    }

    public int getPaymentType() {
        return PaymentType;
    }

    public void setPaymentType(int PaymentType) {
        this.PaymentType = PaymentType;
    }

    public String getCreated() {
        return Created;
    }

    public void setCreated(String Created) {
        this.Created = Created;
    }
}
