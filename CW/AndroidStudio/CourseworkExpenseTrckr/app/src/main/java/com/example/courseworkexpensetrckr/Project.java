package com.example.courseworkexpensetrckr;

public class Project {
    private String projectId;
    private String name;
    private String description;
    private String startDate;
    private String endDate;
    private String manager;
    private String status;
    private double budget;
    private String specialReq;
    private String clientInfo;
    private boolean favorite;
    public Project() {
    }
    public Project(String projectId, String name, String description, String startDate,
                   String endDate, String manager, String status, double budget,
                   String specialReq, String clientInfo, boolean favorite) {
        this.projectId = projectId;
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.manager = manager;
        this.status = status;
        this.budget = budget;
        this.specialReq = specialReq;
        this.clientInfo = clientInfo;
        this.favorite = favorite;
    }

    public String getProjectId() {
        return projectId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getManager() {
        return manager;
    }

    public String getStatus() {
        return status;
    }

    public double getBudget() {
        return budget;
    }

    public String getSpecialReq() {
        return specialReq;
    }

    public String getClientInfo() {
        return clientInfo;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }
    public boolean isFavorite() {return favorite;}

    public void setFavorite(boolean favorite) {this.favorite = favorite;}
}