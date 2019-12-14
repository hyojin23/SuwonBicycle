package com.example.suwonbicycle;

public class SearchDictionary {

    private String rackName;  // 자전거보관소명
    private String roadNameAddress;	// 소재지도로명주소
    private String landBasedAddress;	// 소재지지번주소
    private double latitude;  // 위도
    private double longitude;  // 경도
    private String storageCount;   // 보관대수
    private String installationYear; // 설치년도
    private String installationStle;	 // 설치형태
    private String awningsYn;	 // 차양막설치여부
    private String airInjectorYn; // 공기주입기비치여부
    private String airInjectorType; // 공기주입기유형
    private String repairStandYn; // 수리대설치여부
    private String phoneNumber; // 관리기관전화번호
    private String institutionName	; // 관리기관명

    public String getRackName() {
        return rackName;
    }

    public void setRackName(String rackName) {
        this.rackName = rackName;
    }

    public String getRoadNameAddress() {
        return roadNameAddress;
    }

    public void setRoadNameAddress(String roadNameAddress) {
        this.roadNameAddress = roadNameAddress;
    }

    public String getLandBasedAddress() {
        return landBasedAddress;
    }

    public void setLandBasedAddress(String landBasedAddress) {
        this.landBasedAddress = landBasedAddress;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getStorageCount() {
        return storageCount;
    }

    public void setStorageCount(String storageCount) {
        this.storageCount = storageCount;
    }

    public String getInstallationYear() {
        return installationYear;
    }

    public void setInstallationYear(String installationYear) {
        this.installationYear = installationYear;
    }

    public String getInstallationStle() {
        return installationStle;
    }

    public void setInstallationStle(String installationStle) {
        this.installationStle = installationStle;
    }

    public String getAwningsYn() {
        return awningsYn;
    }

    public void setAwningsYn(String awningsYn) {
        this.awningsYn = awningsYn;
    }

    public String getAirInjectorYn() {
        return airInjectorYn;
    }

    public void setAirInjectorYn(String airInjectorYn) {
        this.airInjectorYn = airInjectorYn;
    }

    public String getAirInjectorType() {
        return airInjectorType;
    }

    public void setAirInjectorType(String airInjectorType) {
        this.airInjectorType = airInjectorType;
    }

    public String getRepairStandYn() {
        return repairStandYn;
    }

    public void setRepairStandYn(String repairStandYn) {
        this.repairStandYn = repairStandYn;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getInstitutionName() {
        return institutionName;
    }

    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }

    public SearchDictionary(String rackName, String landBasedAddress, double latitude, double longitude, String airInjectorYn, String repairStandYn, String phoneNumber, String institutionName) {
        this.rackName = rackName;
        this.landBasedAddress = landBasedAddress;
        this.latitude = latitude;
        this.longitude = longitude;
        this.airInjectorYn = airInjectorYn;
        this.repairStandYn = repairStandYn;
        this.phoneNumber = phoneNumber;
        this.institutionName = institutionName;
    }
}
