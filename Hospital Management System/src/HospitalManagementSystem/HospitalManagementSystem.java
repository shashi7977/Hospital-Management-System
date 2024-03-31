package HospitalManagementSystem;

import java.sql.*;
import java.util.Scanner;

public class HospitalManagementSystem {
    private static final String url = "jdbc:mysql://localhost:3306/hospital_management";
    private static final String username = "root";
    private static final String password = "shashi.mysql@12";

    public static void main(String[] args){
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Drivers Loaded successfully");
        }
        catch (ClassNotFoundException e){
            System.out.println(e.getMessage());
        }
        Scanner scanner = new Scanner(System.in);
        try{
            Connection connection = DriverManager.getConnection(url, username, password);
            Patient patient = new Patient(connection, scanner);
            Doctors doctors = new Doctors(connection);
            while(true){
                System.out.println();
                System.out.println("HOSPITAL MANAGEMENT SYSTEM ");
                System.out.println("1. ADD PATIENT");
                System.out.println("2. VIEW PATIENT");
                System.out.println("3. VIEW DOCTOR");
                System.out.println("4. BOOK APPOINTMENT");
                System.out.println("5. EXIT");
                System.out.print("Enter Your Choice: ");
                int choice = scanner.nextInt();
                switch (choice){
                    case 1:
                        patient.addPatient();// add patinet method or call
                        break;
                    case 2:
                        patient.viewPatient();
                        break;
                    case 3:
                        doctors.viewDoctors();
                        break;
                    case 4:
                        bookAppointment(patient, doctors, connection, scanner);
                        break;
                    case 5:
                        System.out.println("Thank you for Using!!");
                        return;
                    default:
                        System.out.print("!!!!Enter Valid Number!!!");
                }
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }
    public static void bookAppointment(Patient patient ,Doctors doctors, Connection connection, Scanner scanner){
        System.out.print("Enter Patient Id: ");
        int patientId = scanner.nextInt();
        System.out.print("Enter Doctor Id: ");
        int doctorId = scanner.nextInt();
        System.out.print("Enter Appointment Date (YYYY- MM-DD): ");
        String appointmentDate = scanner.next();

        if(patient.getPatientById(patientId) && doctors.getDoctorById(doctorId)){
            if(isDoctorAvailable(doctorId, appointmentDate, connection)){
                String query = "INSERT INTO appointments(patient_id, doctor_id, appointment_date) VALUES(?,?,?);";
                try{
                    PreparedStatement preparedStatement = connection.prepareStatement(query);
                    preparedStatement.setInt(1,patientId);
                    preparedStatement.setInt(2,doctorId);
                    preparedStatement.setString(3,appointmentDate);
                    int affectedRows = preparedStatement.executeUpdate();
                    if(affectedRows > 0){
                        System.out.println("Appointment Date Booked Successfully!!");
                    }
                    else{
                        System.out.println("Failed to Booked Try Again!!");
                    }
                }
                catch (SQLException e){
                    e.printStackTrace();
                }

            }else{
                System.out.println("Doctor Not Available on this date:");
            }
        }
        else{
            System.out.println("Either Patient Id and Doctor Id Not Exists!! ");
        }
    }
    public static boolean isDoctorAvailable(int doctorId, String appointmentDate,Connection connection){
        String query = "SELECT COUNT(*) FROM appointments WHERE doctor_id = ? AND appointment_date = ?;";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1,doctorId);
            preparedStatement.setString(2, appointmentDate);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                int count = resultSet.getInt(1);
                if(count == 0){
                    return true;
                }else{
                    return false;
                }
            }else {
                return false;
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }

        return false;
    }
}
