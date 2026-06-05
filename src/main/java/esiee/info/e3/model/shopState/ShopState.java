package esiee.info.e3.model.shopState;

public sealed interface ShopState
    permits ShopSuccess,
        ShopErrorInvalidItem,
        ShopErrorInventoryFull,
        ShopErrorNoMoneyReroll,
        ShopErrorNoMoneyBuy {}
