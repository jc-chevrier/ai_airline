package fr.ul.miage.ai_airline.data_structure;

import fr.ul.miage.ai_airline.orm.Entity;
import org.jetbrains.annotations.NotNull;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Contexte global la compagnie aérienne.
 */
public class Global extends Entity  {
    //Nom de la table correspondant à l'entité.
    public static String NAME_TABLE = "GLOBAL";
    //Structure de l'entité [attribut -> type].
    public static Map<String, Class> STRUCTURE;

    //Initialisation de la structure.
    static {
        STRUCTURE = new HashMap<String, Class>();
        STRUCTURE.put("ID", Integer.class);
        STRUCTURE.put("START_DATE", Date.class);
        STRUCTURE.put("END_DATE", Date.class);
        STRUCTURE.put("BALANCE", Double.class);
        STRUCTURE.put("BALANCE_INITIALIZATION", Double.class);
        STRUCTURE.put("FUEL_COST", Double.class);
        STRUCTURE.put("CONSOMMATION_FUEL_COST", Double.class);
        STRUCTURE.put("MAINTENANCE_COST", Double.class);
        STRUCTURE.put("PASSENGER_TAX", Double.class);
        STRUCTURE.put("FIRST_CLASS_RATE", Double.class);
        STRUCTURE.put("BUSINESS_CLASS_RATE", Double.class);
        STRUCTURE.put("ECONOMIC_CLASS_RATE", Double.class);
        STRUCTURE.put("PLACE_PRICE_FACTOR", Double.class);
        STRUCTURE.put("PERCENTAGE_CANCELLATION", Double.class);
        STRUCTURE.put("OFFSET_PLACES_RECOMMANDATION", Integer.class);
        STRUCTURE.put("OFFSET_HOURS_RECOMMANDATION", Integer.class);
    }

    public Global() {
        super();
    }

    public Global(@NotNull Map<String, Object> attributes) {
        super(attributes);
    }

    public Date getStartDate() {
        return (Date) get("START_DATE");
    }

    public void setStartDate(@NotNull Date startDate) {
        set("START_DATE", startDate);
    }

    public Date getEndDate() {
        return (Date) get("END_DATE");
    }

    public void setEndDate(@NotNull Date endDate) {
        set("END_DATE", endDate);
    }

    public Double getBalance() {
        return (Double) get("BALANCE");
    }

    public void setBalance(@NotNull Double balance) {
        set("BALANCE", balance);
    }

    public Double getBalanceInitialization() {
        return (Double) get("BALANCE_INITIALIZATION");
    }

    public void setBalanceInitialization(@NotNull Double balanceInitialization) {
        set("BALANCE_INITIALIZATION", balanceInitialization);
    }

    public Double getFuelCost() {
        return (Double) get("FUEL_COST");
    }

    public void setFuelCost(@NotNull Double fuelCost) {
        set("FUEL_COST", fuelCost);
    }

    public Double getConsommationFuelCost() {
        return (Double) get("CONSOMMATION_FUEL_COST");
    }

    public void setConsommationFuelCost(@NotNull Double consommationFuelCost) {
        set("CONSOMMATION_FUEL_COST", consommationFuelCost);
    }

    public Double getMaintenanceCost() {
        return (Double) get("MAINTENANCE_COST");
    }

    public void setMaintenanceCost(@NotNull Double maintenanceCost) {
        set("MAINTENANCE_COST", maintenanceCost);
    }

    public Double getPassengerTax() {
        return (Double) get("PASSENGER_TAX");
    }

    public void setPassengerTax(@NotNull Double passengerTax) {
        set("PASSENGER_TAX", passengerTax);
    }

    public Double getFirstClassRate() {
        return (Double) get("FIRST_CLASS_RATE");
    }

    public void setFirstClassRate(@NotNull Double firstClassRate) {
        set("FIRST_CLASS_RATE", firstClassRate);
    }

    public Double getBusinessClassRate() {
        return (Double) get("BUSINESS_CLASS_RATE");
    }

    public void setBusinessClassRate(@NotNull Double businessClassRate) {
        set("BUSINESS_CLASS_RATE", businessClassRate);
    }

    public Double getEconomicClassRate() {
        return (Double) get("ECONOMIC_CLASS_RATE");
    }

    public void setEconomicClassRate(@NotNull Double economicClassRate) {
        set("ECONOMIC_CLASS_RATE", economicClassRate);
    }

    public Double getPlacePriceFactor() {
        return (Double) get("PLACE_PRICE_FACTOR");
    }

    public void setPlacePriceFactor(@NotNull Double placePriceFactor) {
        set("PLACE_PRICE_FACTOR", placePriceFactor);
    }

    public Double getPercentageCancellation() {
        return (Double) get("PERCENTAGE_CANCELLATION");
    }

    public void setPercentageCancellation(@NotNull Double percentageCancellation) {
        set("PERCENTAGE_CANCELLATION", percentageCancellation);
    }

    public Integer getOffsetPLacesRecommandation() {
        return (Integer) get("OFFSET_PLACES_RECOMMANDATION");
    }

    public void setOffsetPlacesRecommandation(@NotNull Integer offsetPlacesRecommandation) {
        set("OFFSET_PLACES_RECOMMANDATION", offsetPlacesRecommandation);
    }

    public Integer getOffsetHoursRecommandation() {
        return (Integer) get("OFFSET_HOURS_RECOMMANDATION");
    }

    public void setOffsetHoursRecommandation(@NotNull Double offsetHoursRecommandation) {
        set("OFFSET_HOURS_RECOMMANDATION", offsetHoursRecommandation);
    }

    /**
     * Incrémenter le solde de la compagnie
     * aérienne.
     *
     * @param amount
     */
    public void incrementBalance(@NotNull Double amount) {
        setBalance(getBalance() + amount);
    }
}