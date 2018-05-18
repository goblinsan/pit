package pit;

import pit.bank.Bank;
import pit.errors.BidOutOfBounds;
import pit.errors.ErrorMessages;
import pit.errors.OfferOutOfBounds;

class TradeValidation {

    private Bank bank;

    TradeValidation(Bank bank) {
        this.bank = bank;
    }

    boolean isValidOffer(Offer offer) {
        if (offer.getAmount() < 1) {
            throw new OfferOutOfBounds(GameResponse.INVALID, ErrorMessages.OFFER_LESS_THAN_ZERO);
        }
        if (playerDoesNotHaveEnoughMatchingCards(offer.getPlayer(), offer.getAmount())) {
            throw new OfferOutOfBounds(GameResponse.INVALID, ErrorMessages.PLAYER_CANNOT_SATISFY_OFFER);
        }
        return true;
    }

    boolean isValidBid(Bid bid) {
        if (bid.getAmount() < 1) {
            throw new BidOutOfBounds(GameResponse.INVALID, ErrorMessages.BID_LESS_THAN_ZERO);
        }
        if (!playerCanSatisfyTrade(bid.getRequester(), bid.getAmount(), bid.getCommodity())) {
            throw new BidOutOfBounds(GameResponse.INVALID, ErrorMessages.PLAYER_CANNOT_SATISFY_BID);
        }
        return true;
    }

    boolean playerCanSatisfyTrade(Player player, int amount, Commodity commodity) {
        return bank.getHoldings().get(player).get(commodity) >= amount;
    }

    private boolean playerDoesNotHaveEnoughMatchingCards(Player player, int amount) {
        return getMaxMatchingSet(player) < amount;
    }

    private int getMaxMatchingSet(Player player) {
        return bank.getHoldings().get(player).values().stream().max(Integer::compareTo).orElse(0);
    }

}