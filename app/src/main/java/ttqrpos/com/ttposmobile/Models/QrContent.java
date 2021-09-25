package ttqrpos.com.ttposmobile.Models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.pixplicity.easyprefs.library.Prefs;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import gmp3.droid.printer.DateTime;
import hugin.common.lib.d10.PaymentResponse;

@Table(name="QrContent")
public class QrContent extends Model {

    @Column(name = "userID")
    private int userID;

    @Column(name = "actDate")
    private Date actDate;

    @Column(name = "actDateText")
    private String actDateText;

    @Column(name = "terminalID")
    private String terminalID;

    @Column(name = "serialNo")
    private String serialNo;

    @Column(name = "refNo")
    private String refNo;

    @Column(name = "amount")
    private double amount;

    @Column(name = "isSended")
    private int isSended;

    @Column(name = "remoteID")
    private int remoteID;

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getTerminalID() {
        return terminalID;
    }

    public void setTerminalID(String terminalID) {
        this.terminalID = terminalID;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public String getRefNo() {
        return refNo;
    }

    public void setRefNo(String refNo) {
        this.refNo = refNo;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public int getIsSended() {
        return isSended;
    }

    public void setIsSended(int isSended) {
        this.isSended = isSended;
    }
    public Date getActDate() {
        return actDate;
    }

    public void setActDate(Date actDate) {
        this.actDate = actDate;
    }


    public int getRemoteID() {
        return remoteID;
    }

    public void setRemoteID(int remoteID) {
        this.remoteID = remoteID;
    }

    public String getActDateText() {
        return actDateText;
    }

    public void setActDateText(String actDateText) {
        this.actDateText = actDateText;
    }


    public QrContent getFromHuginResponse (PaymentResponse response){
        QrContent qrContent=new QrContent();
        Calendar cal = Calendar.getInstance();
        qrContent.setActDate(cal.getTime());
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        qrContent.setActDateText(sdf.format(qrContent.getActDate()));
        qrContent.setUserID(Prefs.getInt("userID",1));
        qrContent.setTerminalID(response.getTerminalId());
        qrContent.setRefNo(response.getRefNo());
        qrContent.setSerialNo(response.getSerialNo());
        qrContent.setAmount(response.getAmount());

        return qrContent;
    }

    public static List<QrContent> getListByDate (Date filterDate){
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date startDate=new Date(filterDate.getYear(),filterDate.getMonth(),filterDate.getDate());

        return new Select()
                .from(QrContent.class)
                .where("ActDateText = ?", sdf.format(startDate))
                .orderBy("ActDate DESC")
                .execute();
    }
}
