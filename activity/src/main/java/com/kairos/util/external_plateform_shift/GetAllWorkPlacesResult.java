package com.kairos.util.external_plateform_shift;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

/**
 * Created by oodles on 14/12/16.
 */
public class GetAllWorkPlacesResult {
    @JacksonXmlProperty
    private String ExternalNodeId;
    @JacksonXmlProperty
    private Boolean IsParent;
    @JacksonXmlProperty
    private String UnitParentWorkPlaceId;
    @JacksonXmlProperty
    private String ArrayOfDayTypeUseBreak;
    @JacksonXmlProperty
    private String ContractId;
    @JacksonXmlProperty
    private String ParentWorkPlaceName;
    @JacksonXmlProperty
    private String PathName;
    @JacksonXmlProperty
    private String Activated;
    @JacksonXmlProperty
    private Contract Contract;
    @JacksonXmlProperty
    private String ParentNamePath;
    @JacksonXmlProperty
    private String Path;
    @JacksonXmlProperty
    private String TCSchedulerActId;
    @JacksonXmlProperty
    private String Name;
    @JacksonXmlProperty
    private Long ParentWorkPlaceID;
    @JacksonXmlProperty
    private String ShortName;
    @JacksonXmlProperty
    private String NamePath;
    @JacksonXmlProperty
    private String Level;
    @JacksonXmlProperty
    private Long Id;
    @JacksonXmlProperty
    private String Reference;
    @JacksonXmlProperty
    private String IsExportable;
    @JacksonXmlProperty
    private String CostPlace;

    public GetAllWorkPlacesResult() {
    }

    public String getExternalNodeId() {
        return ExternalNodeId;
    }

    public void setExternalNodeId(String ExternalNodeId) {
        this.ExternalNodeId = ExternalNodeId;
    }

    public Boolean getIsParent() {
        return IsParent;
    }

    public void setIsParent(Boolean IsParent) {
        this.IsParent = IsParent;
    }

    public String getUnitParentWorkPlaceId() {
        return UnitParentWorkPlaceId;
    }

    public void setUnitParentWorkPlaceId(String UnitParentWorkPlaceId) {
        this.UnitParentWorkPlaceId = UnitParentWorkPlaceId;
    }

    public String getArrayOfDayTypeUseBreak() {
        return ArrayOfDayTypeUseBreak;
    }

    public void setArrayOfDayTypeUseBreak(String ArrayOfDayTypeUseBreak) {
        this.ArrayOfDayTypeUseBreak = ArrayOfDayTypeUseBreak;
    }

    public String getContractId() {
        return ContractId;
    }

    public void setContractId(String ContractId) {
        this.ContractId = ContractId;
    }

    public String getParentWorkPlaceName() {
        return ParentWorkPlaceName;
    }

    public void setParentWorkPlaceName(String ParentWorkPlaceName) {
        this.ParentWorkPlaceName = ParentWorkPlaceName;
    }

    public String getPathName() {
        return PathName;
    }

    public void setPathName(String PathName) {
        this.PathName = PathName;
    }

    public String getActivated() {
        return Activated;
    }

    public void setActivated(String Activated) {
        this.Activated = Activated;
    }

    public Contract getContract() {
        return Contract;
    }

    public void setContract(Contract Contract) {
        this.Contract = Contract;
    }

    public String getParentNamePath() {
        return ParentNamePath;
    }

    public void setParentNamePath(String ParentNamePath) {
        this.ParentNamePath = ParentNamePath;
    }

    public String getPath() {
        return Path;
    }

    public void setPath(String Path) {
        this.Path = Path;
    }

    public String getTCSchedulerActId() {
        return TCSchedulerActId;
    }

    public void setTCSchedulerActId(String TCSchedulerActId) {
        this.TCSchedulerActId = TCSchedulerActId;
    }

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public Long getParentWorkPlaceID() {
        return ParentWorkPlaceID;
    }

    public void setParentWorkPlaceID(Long ParentWorkPlaceID) {
        this.ParentWorkPlaceID = ParentWorkPlaceID;
    }

    public String getShortName() {
        return ShortName;
    }

    public void setShortName(String ShortName) {
        this.ShortName = ShortName;
    }

    public String getNamePath() {
        return NamePath;
    }

    public void setNamePath(String NamePath) {
        this.NamePath = NamePath;
    }

    public String getLevel() {
        return Level;
    }

    public void setLevel(String Level) {
        this.Level = Level;
    }

    public Long getId() {
        return Id;
    }

    public void setId(Long Id) {
        this.Id = Id;
    }

    public String getReference() {
        return Reference;
    }

    public void setReference(String Reference) {
        this.Reference = Reference;
    }

    public String getIsExportable() {
        return IsExportable;
    }

    public void setIsExportable(String IsExportable) {
        this.IsExportable = IsExportable;
    }

    public String getCostPlace() {
        return CostPlace;
    }

    public void setCostPlace(String CostPlace) {
        this.CostPlace = CostPlace;
    }

    @Override
    public String toString() {
        return "ClassPojo [ExternalNodeId = " + ExternalNodeId + ", IsParent = " + IsParent + ", UnitParentWorkPlaceId = " + UnitParentWorkPlaceId + ", ArrayOfDayTypeUseBreak = " + ArrayOfDayTypeUseBreak + ", ContractId = " + ContractId + ", ParentWorkPlaceName = " + ParentWorkPlaceName + ", PathName = " + PathName + ", Activated = " + Activated + ", Contract = " + Contract + ", ParentNamePath = " + ParentNamePath + ", Path = " + Path + ", TCSchedulerActId = " + TCSchedulerActId + ", Name = " + Name + ", ParentWorkPlaceID = " + ParentWorkPlaceID + ", ShortName = " + ShortName + ", NamePath = " + NamePath + ", Level = " + Level + ", Id = " + Id + ", Reference = " + Reference + ", IsExportable = " + IsExportable + ", CostPlace = " + CostPlace + "]";
    }
}
