package com.kairos.util.external_plateform_shift;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

/**
 * Created by oodles on 14/12/16.
 */
public class Activity {
    @JacksonXmlProperty
    private String Type;
    @JacksonXmlProperty
    private String Usable;
    @JacksonXmlProperty
    private String ExternalActivityId;
    @JacksonXmlProperty
    private String GotTimes;
    @JacksonXmlProperty
    private String UseTimeRules;
    @JacksonXmlProperty
    private String BalanceDayType;
    @JacksonXmlProperty
    private String TimeMethod;
    @JacksonXmlProperty
    private String BalanceType;
    @JacksonXmlProperty
    private Boolean IsBreak;
    @JacksonXmlProperty
    private String ArrayOfSkill;
    @JacksonXmlProperty
    private String NegativeDayBalance;
    @JacksonXmlProperty
    private String ArrayOfRuleContract;
    @JacksonXmlProperty
    private String UpdateTypeFlag;
    @JacksonXmlProperty
    private String UseInSchedType;
    @JacksonXmlProperty
    private Boolean IsWork;
    @JacksonXmlProperty
    private String FixedLenShift;
    @JacksonXmlProperty
    private String CombinedWith;
    @JacksonXmlProperty
    private String UseDefSched;
    @JacksonXmlProperty
    private String Name;
    @JacksonXmlProperty
    private String PrioNo;
    @JacksonXmlProperty
    private Boolean IsStaffing;
    @JacksonXmlProperty
    private String TimeDefSched;
    @JacksonXmlProperty
    private String Symbol;
    @JacksonXmlProperty
    private String ShortName;
    @JacksonXmlProperty
    private String MultiplyTimeWith;
    @JacksonXmlProperty
    private String Id;
    @JacksonXmlProperty
    private String ArrayOfDayTypeUsable;
    @JacksonXmlProperty
    private String Reference;
    @JacksonXmlProperty
    private String UpdateDate;
    @JacksonXmlProperty
    private Boolean IsExportable;
    @JacksonXmlProperty
    private String CostPlace;
    @JacksonXmlProperty
    private Boolean WholeDay;
    @JacksonXmlProperty
    private Boolean IsPresence;
    @JacksonXmlProperty
    private String ArrayOfDayTypeCalc;

    public Activity() {

    }

    public String getType() {
        return Type;
    }

    public void setType(String Type) {
        this.Type = Type;
    }

    public String getUsable() {
        return Usable;
    }

    public void setUsable(String Usable) {
        this.Usable = Usable;
    }

    public String getExternalActivityId() {
        return ExternalActivityId;
    }

    public void setExternalActivityId(String ExternalActivityId) {
        this.ExternalActivityId = ExternalActivityId;
    }

    public String getGotTimes() {
        return GotTimes;
    }

    public void setGotTimes(String GotTimes) {
        this.GotTimes = GotTimes;
    }

    public String getUseTimeRules() {
        return UseTimeRules;
    }

    public void setUseTimeRules(String UseTimeRules) {
        this.UseTimeRules = UseTimeRules;
    }

    public String getBalanceDayType() {
        return BalanceDayType;
    }

    public void setBalanceDayType(String BalanceDayType) {
        this.BalanceDayType = BalanceDayType;
    }

    public String getTimeMethod() {
        return TimeMethod;
    }

    public void setTimeMethod(String TimeMethod) {
        this.TimeMethod = TimeMethod;
    }

    public String getBalanceType() {
        return BalanceType;
    }

    public void setBalanceType(String BalanceType) {
        this.BalanceType = BalanceType;
    }

    public Boolean getIsBreak() {
        return IsBreak;
    }

    public void setIsBreak(Boolean IsBreak) {
        this.IsBreak = IsBreak;
    }

    public String getArrayOfSkill() {
        return ArrayOfSkill;
    }

    public void setArrayOfSkill(String ArrayOfSkill) {
        this.ArrayOfSkill = ArrayOfSkill;
    }

    public String getNegativeDayBalance() {
        return NegativeDayBalance;
    }

    public void setNegativeDayBalance(String NegativeDayBalance) {
        this.NegativeDayBalance = NegativeDayBalance;
    }

    public String getArrayOfRuleContract() {
        return ArrayOfRuleContract;
    }

    public void setArrayOfRuleContract(String ArrayOfRuleContract) {
        this.ArrayOfRuleContract = ArrayOfRuleContract;
    }

    public String getUpdateTypeFlag() {
        return UpdateTypeFlag;
    }

    public void setUpdateTypeFlag(String UpdateTypeFlag) {
        this.UpdateTypeFlag = UpdateTypeFlag;
    }

    public String getUseInSchedType() {
        return UseInSchedType;
    }

    public void setUseInSchedType(String UseInSchedType) {
        this.UseInSchedType = UseInSchedType;
    }

    public Boolean getIsWork() {
        return IsWork;
    }

    public void setIsWork(Boolean IsWork) {
        this.IsWork = IsWork;
    }

    public String getFixedLenShift() {
        return FixedLenShift;
    }

    public void setFixedLenShift(String FixedLenShift) {
        this.FixedLenShift = FixedLenShift;
    }

    public String getCombinedWith() {
        return CombinedWith;
    }

    public void setCombinedWith(String CombinedWith) {
        this.CombinedWith = CombinedWith;
    }

    public String getUseDefSched() {
        return UseDefSched;
    }

    public void setUseDefSched(String UseDefSched) {
        this.UseDefSched = UseDefSched;
    }

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public String getPrioNo() {
        return PrioNo;
    }

    public void setPrioNo(String PrioNo) {
        this.PrioNo = PrioNo;
    }

    public Boolean getIsStaffing() {
        return IsStaffing;
    }

    public void setIsStaffing(Boolean IsStaffing) {
        this.IsStaffing = IsStaffing;
    }

    public String getTimeDefSched() {
        return TimeDefSched;
    }

    public void setTimeDefSched(String TimeDefSched) {
        this.TimeDefSched = TimeDefSched;
    }

    public String getSymbol() {
        return Symbol;
    }

    public void setSymbol(String Symbol) {
        this.Symbol = Symbol;
    }

    public String getShortName() {
        return ShortName;
    }

    public void setShortName(String ShortName) {
        this.ShortName = ShortName;
    }

    public String getMultiplyTimeWith() {
        return MultiplyTimeWith;
    }

    public void setMultiplyTimeWith(String MultiplyTimeWith) {
        this.MultiplyTimeWith = MultiplyTimeWith;
    }

    public String getId() {
        return Id;
    }

    public void setId(String Id) {
        this.Id = Id;
    }

    public String getArrayOfDayTypeUsable() {
        return ArrayOfDayTypeUsable;
    }

    public void setArrayOfDayTypeUsable(String ArrayOfDayTypeUsable) {
        this.ArrayOfDayTypeUsable = ArrayOfDayTypeUsable;
    }

    public String getReference() {
        return Reference;
    }

    public void setReference(String Reference) {
        this.Reference = Reference;
    }

    public String getUpdateDate() {
        return UpdateDate;
    }

    public void setUpdateDate(String UpdateDate) {
        this.UpdateDate = UpdateDate;
    }

    public Boolean getIsExportable() {
        return IsExportable;
    }

    public void setIsExportable(Boolean IsExportable) {
        this.IsExportable = IsExportable;
    }

    public String getCostPlace() {
        return CostPlace;
    }

    public void setCostPlace(String CostPlace) {
        this.CostPlace = CostPlace;
    }

    public Boolean getWholeDay() {
        return WholeDay;
    }

    public void setWholeDay(Boolean WholeDay) {
        this.WholeDay = WholeDay;
    }

    public Boolean getIsPresence() {
        return IsPresence;
    }

    public void setIsPresence(Boolean IsPresence) {
        this.IsPresence = IsPresence;
    }

    public String getArrayOfDayTypeCalc() {
        return ArrayOfDayTypeCalc;
    }

    public void setArrayOfDayTypeCalc(String ArrayOfDayTypeCalc) {
        this.ArrayOfDayTypeCalc = ArrayOfDayTypeCalc;
    }

    @Override
    public String toString() {
        return "ClassPojo [Type = " + Type + ", Usable = " + Usable + ", ExternalActivityId = " + ExternalActivityId + ", GotTimes = " + GotTimes + ", UseTimeRules = " + UseTimeRules + ", BalanceDayType = " + BalanceDayType + ", TimeMethod = " + TimeMethod + ", BalanceType = " + BalanceType + ", IsBreak = " + IsBreak + ", ArrayOfSkill = " + ArrayOfSkill + ", NegativeDayBalance = " + NegativeDayBalance + ", ArrayOfRuleContract = " + ArrayOfRuleContract + ", UpdateTypeFlag = " + UpdateTypeFlag + ", UseInSchedType = " + UseInSchedType + ", IsWork = " + IsWork + ", FixedLenShift = " + FixedLenShift + ", CombinedWith = " + CombinedWith + ", UseDefSched = " + UseDefSched + ", Name = " + Name + ", PrioNo = " + PrioNo + ", IsStaffing = " + IsStaffing + ", TimeDefSched = " + TimeDefSched + ", Symbol = " + Symbol + ", ShortName = " + ShortName + ", MultiplyTimeWith = " + MultiplyTimeWith + ", Id = " + Id + ", ArrayOfDayTypeUsable = " + ArrayOfDayTypeUsable + ", Reference = " + Reference + ", UpdateDate = " + UpdateDate + ", IsExportable = " + IsExportable + ", CostPlace = " + CostPlace + ", WholeDay = " + WholeDay + ", IsPresence = " + IsPresence + ", ArrayOfDayTypeCalc = " + ArrayOfDayTypeCalc + "]";
    }
}
