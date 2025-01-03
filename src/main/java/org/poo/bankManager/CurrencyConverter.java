package org.poo.bankManager;

import org.poo.instances.ExchangeRate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

class CurrencyNode {
    private String toCurrency;
    private double rate;

    CurrencyNode(final String toCurrency, final double rate) {
        this.toCurrency = toCurrency;
        this.rate = rate;
    }

    public String getToCurrency() {
        return toCurrency;
    }

    public void setToCurrency(final String toCurrency) {
        this.toCurrency = toCurrency;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(final double rate) {
        this.rate = rate;
    }
}

class Graph {
    private Map<String, ArrayList<CurrencyNode>> graph;

    Graph() {
        this.graph = new HashMap<>();
    }

    /**
     * Add an exchange rate to the graph
     * @param fromCurrency the currency to convert from
     * @param toCurrency the currency to convert to
     * @param rate the exchange rate
     */
    public void addExchangeRate(final String fromCurrency, final String toCurrency,
                                final double rate) {
        // if the currency is not in the graph, add it
        if (!this.graph.containsKey(fromCurrency)) {
            graph.put(fromCurrency, new ArrayList<>());
        }

        // add the exchange rate to the graph
        ArrayList<CurrencyNode> fromCurrencyNeighbor = graph.get(fromCurrency);
        fromCurrencyNeighbor.add(new CurrencyNode(toCurrency, rate));

        // do the same in the opposite direction
        if (!this.graph.containsKey(toCurrency)) {
            graph.put(toCurrency, new ArrayList<>());
        }

        ArrayList<CurrencyNode> toCurrencyNeighbor = graph.get(toCurrency);
        toCurrencyNeighbor.add(new CurrencyNode(fromCurrency, 1.0 / rate));
    }

    /**
     * Helper function to convert the amount from a currency to another.
     * @param fromCurrency
     * @param toCurrency
     * @return
     */
    public double convert(final String fromCurrency, final String toCurrency) {
        // if the currencies are the same, return 1 (the amount remains the same)
        if (fromCurrency.equals(toCurrency)) {
            return 1.0;
        }

        Map<String, Boolean> visited = new HashMap<>();

        // call dfs to find the conversion rate
        return dfs(fromCurrency, toCurrency, visited);
    }

    /**
     * Dfs to find the conversion rate.
     * @param currentCurrency the current currency
     * @param targetCurrency the target currency
     * @param visited the visited nodes
     * @return the conversion rate
     */
    private double dfs(final String currentCurrency, final String targetCurrency,
                       final Map<String, Boolean> visited) {
        // the node has already been visited, return 0
        if (visited.containsKey(currentCurrency) && visited.get(currentCurrency)) {
            return 0;
        }

        // mark the node as visited
        visited.put(currentCurrency, true);

        // if the node has any neighbours and one of them is the target currency, return his rate
        if (this.graph.containsKey(currentCurrency)) {
            for (CurrencyNode node : this.graph.get(currentCurrency)) {
                if (node.getToCurrency().equals(targetCurrency)) {
                    return node.getRate();
                }
            }
        }

        // call dfs on the neighbours
        if (this.graph.containsKey(currentCurrency)) {
            for (CurrencyNode node : this.graph.get(currentCurrency)) {
                double result = dfs(node.getToCurrency(), targetCurrency, visited);
                // if one of the neighbours has the target currency as a neighbour, return the rate
                // (we have found the conversion rate)
                if (result != 0) {
                    return node.getRate() * result;
                }
            }
        }

        // if the target currency is not found, return 0
        return 0;
    }
}

public final class CurrencyConverter {
    private CurrencyConverter() {
    }

    private static Graph graph;

    /**
     * Set the bank with the exchange rates
     * @param exchangeRates the exchange rates
     */
    public static void setBank(final ArrayList<ExchangeRate> exchangeRates) {
        graph = new Graph();
        // add the exchange rates to the graph
        for (ExchangeRate exchangeRate : exchangeRates) {
            graph.addExchangeRate(exchangeRate.getFrom(), exchangeRate.getTo(),
                    exchangeRate.getRate());
        }
    }

    /**
     * Convert the amount from a currency to another
     * @param fromCurrency the currency to convert from
     * @param toCurrency the currency to convert to
     * @param amount the amount to convert
     * @return the converted amount
     */
    public static double convert(final String fromCurrency, final String toCurrency,
                                 final double amount) {
        return graph.convert(fromCurrency, toCurrency) * amount;
    }
}
