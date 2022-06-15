package pl.edu.pbs.serwiskomputerowy.ui;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.component.template.Id;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import pl.edu.pbs.serwiskomputerowy.model.Client;
import pl.edu.pbs.serwiskomputerowy.model.Equipment;
import pl.edu.pbs.serwiskomputerowy.service.ClientService;
import pl.edu.pbs.serwiskomputerowy.service.EquipmentService;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * A Designer generated component for the home-page template.
 *
 * Designer will add and remove fields with @Id mappings but
 * does not overwrite or otherwise change this file.
 */
@Tag("home-page")
@JsModule("./home-page.ts")
@Route("")
public class HomePage extends LitTemplate {
    @Id("nameCB")
    private ComboBox<String> nameCB;
    @Id("fixedCB")
    private ComboBox<String> fixedCB;
    @Id("resetFiltersBT")
    private Button resetFiltersBT;
    @Id("equipmentGrid")
    private Grid<Equipment> equipmentGrid;
    @Id("clientGrid")
    private Grid<Client> clientGrid;
    @Id("issuedDP")
    private DatePicker issuedDP;
    @Id("admissionDP")
    private DatePicker admissionDP;
    @Id("clientTF")
    private TextField clientTF;

    private final EquipmentService equipmentService;
    private List<Equipment> equipmentList;

    private final ClientService clientService;
    private List<Client> clientList;

    private Client pickedClient = null;

    /**
     * Creates a new HomePage.
     */
    public HomePage(EquipmentService equipmentService, ClientService clientService) {
        this.equipmentService = equipmentService;
        this.clientService = clientService;

        equipmentList = equipmentService.getAllEquipment();
        clientList = clientService.getAllClients();

        equipmentGrid.addColumn(equipment -> equipment.getClientNameFromEquipment(clientService)).setHeader("Klient");
        equipmentGrid.addColumn(Equipment::getEquipmentName).setHeader("Nazwa sprzętu");
        equipmentGrid.addColumn(new LocalDateTimeRenderer<>(Equipment::getEquipmentAdmissionDate), "dd/MM/yyyy HH:mm").setHeader("Data przyjęcia");
        equipmentGrid.addColumn(Equipment::getEquipmentClientNotes).setHeader("Informacje od klienta");
        equipmentGrid.addColumn((value) -> {
            if(value.isEquipmentIsFixed()){
                return "Naprawiony";
            } else {
                return "W naprawie";
            }
        }).setHeader("Naprawiony");
        equipmentGrid.addColumn(new LocalDateTimeRenderer<>(Equipment::getEquipmentIssueDate), "dd/MM/yyyy HH:mm").setHeader("Data wydania");
        equipmentGrid.addColumn(Equipment::getEquipmentRepairNotes).setHeader("Notatki z naprawy");
        equipmentGrid.addColumn(Equipment::getEquipmentRepairCost).setHeader("Koszt naprawy");
        equipmentGrid.getColumns().forEach(col -> col.setAutoWidth(true));

        clientGrid.addColumn(Client::getClientName).setHeader("Nazwa klienta");
        clientGrid.addColumn(Client::getClientMail).setHeader("Mail klienta");
        clientGrid.addColumn(Client::getClientPhone).setHeader("Telefon klienta");
        clientGrid.getColumns().forEach(col -> col.setAutoWidth(true));

        updateGrids();

        nameCB.setItems(equipmentList.stream().map(Equipment::getEquipmentName).distinct().collect(Collectors.toList()));
        fixedCB.setItems("Tak", "Nie");
    }

    @PostConstruct
    private void init() {
        resetFiltersBT.addClickListener(buttonClickEvent -> {
            nameCB.setValue(null);
            pickedClient = null;
            fixedCB.setValue(null);
            issuedDP.setValue(null);
            admissionDP.setValue(null);
            nameCB.setItems(equipmentList.stream().map(Equipment::getEquipmentName).distinct().collect(Collectors.toList()));
            refreshEquipmentList();
        });

        nameCB.addValueChangeListener(value -> {
            if (value != null) {
                refreshEquipmentList();
            }
        });
        fixedCB.addValueChangeListener(value -> {
            if (value != null) {
                refreshEquipmentList();
            }
        });
        issuedDP.addValueChangeListener(value -> {
            if (value != null) {
                refreshEquipmentList();
            }
        });
        admissionDP.addValueChangeListener(value -> {
            if (value != null) {
                refreshEquipmentList();
            }
        });

        clientTF.addValueChangeListener(event -> updateClientList());
        clientTF.setValueChangeMode(ValueChangeMode.LAZY);

        clientGrid.asSingleSelect().addValueChangeListener(event -> handleClientSelect(event.getValue()));
    }

    private void handleClientSelect(Client value) {
        if(value == null){
            pickedClient = null;
            nameCB.setItems(equipmentList.stream().map(Equipment::getEquipmentName).distinct().collect(Collectors.toList()));
            refreshEquipmentList();
        } else {
            pickedClient = value;
            nameCB.setItems(equipmentService.getAllEquipment().stream().filter(equipment -> Objects.equals(equipment.getClientID(), value.getClientID())).map(Equipment::getEquipmentName).distinct().collect(Collectors.toList()));
            refreshEquipmentList();
        }
    }

    private void updateClientList() {
        clientList = clientService.getAllClients();
        clientList = clientList.stream().filter(client -> client.getClientName().contains(clientTF.getValue())).collect(Collectors.toList());
        updateGrids();
    }

    private void refreshEquipmentList(){
        boolean isName = nameCB.getValue() != null;
        boolean isClient = (pickedClient != null);
        boolean isIssued = issuedDP.getValue() != null;
        boolean isAdmissioned = admissionDP.getValue() != null;
        boolean isFixed = Objects.equals(fixedCB.getValue(), "Tak");
        boolean isFixedSelected = fixedCB.getValue() != null;

        equipmentList = equipmentService.getAllEquipment();

        if(isName){
            equipmentList = equipmentList.stream().filter(equipment -> Objects.equals(equipment.getEquipmentName(), nameCB.getValue())).collect(Collectors.toList());
        }
        if(isClient){
            equipmentList = equipmentList.stream().filter(equipment -> Objects.equals(equipment.getClientID(), pickedClient.getClientID())).collect(Collectors.toList());
        }
        if(isFixedSelected){
            equipmentList = equipmentList.stream().filter(equipment -> Objects.equals(equipment.isEquipmentIsFixed(), isFixed)).collect(Collectors.toList());
        }
        if(isIssued){
            equipmentList = equipmentList.stream().filter(equipment -> Objects.equals(equipment.getEquipmentIssueDate().toLocalDate(), issuedDP.getValue())).collect(Collectors.toList());
        }
        if(isAdmissioned){
            equipmentList = equipmentList.stream().filter(equipment -> Objects.equals(equipment.getEquipmentAdmissionDate().toLocalDate(), admissionDP.getValue())).collect(Collectors.toList());
        }

        updateEquipmentGrid();
    }

    private void updateGrids(){
        equipmentGrid.setItems(equipmentList);
        clientGrid.setItems(clientList);
    }

    private void updateEquipmentGrid(){
        equipmentGrid.setItems(equipmentList);
    }

}
