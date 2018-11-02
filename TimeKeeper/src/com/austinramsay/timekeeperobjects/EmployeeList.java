package com.austinramsay.timekeeperobjects;

import java.io.Serializable;
import java.util.ArrayList;

public class EmployeeList implements Serializable {





    private ArrayList<Employee> employees;
    public EmployeeList() {
        this.employees = new ArrayList<>();
    }





    public void setEmployees(ArrayList<Employee> employees) {

        this.employees = employees;

    }





    public ArrayList<Employee> getEmployees() {

        return this.employees;

    }


}
