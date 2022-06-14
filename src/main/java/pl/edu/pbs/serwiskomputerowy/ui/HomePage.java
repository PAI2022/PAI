package pl.edu.pbs.serwiskomputerowy.ui;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.component.template.Id;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;
import com.vaadin.flow.router.Route;
import pl.edu.pbs.serwiskomputerowy.model.Client;
import pl.edu.pbs.serwiskomputerowy.model.Equipment;
import pl.edu.pbs.serwiskomputerowy.service.ClientService;
import pl.edu.pbs.serwiskomputerowy.service.EquipmentService;

import java.util.List;

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
    @Id("equipmentGrid")
    private Grid<Equipment> equipmentGrid;
    @Id("clientGrid")
    private Grid<Client> clientGrid;

    private final EquipmentService equipmentService;
    private List<Equipment> equipmentList;

    private final ClientService clientService;
    private List<Client> clientList;

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
    }

    private void updateGrids(){
        equipmentGrid.setItems(equipmentList);
        clientGrid.setItems(clientList);
    }

}
