import React, {useState} from 'react'
import CostInfo from './CostInfo';
import ApplePayment from './ApplePayment';
import CardPayment from './CardPayment';
import { loadStripe } from '@stripe/stripe-js';
import { Elements } from '@stripe/react-stripe-js';
// import { CreditCard, ChevronDown } from 'lucide-react';




export default function CollectPaymentDetails () {

    const stripePromise = loadStripe('pk_test_51RECoQHQqjXOU2qoZDXortaBrRL9Mo0CFhmK4vZbApuVG3xBcfxyFie0mCi6SrvAFDOp3GDFRkbIk7m823Jkrv9z00yd8ZkF93');

    const total = 145.00;

    const [currency, setCurrency] = useState('gbp');

    const currencySymbol = {
        "gbp": '£',
        "usd": '$',
        "eur": '€',
        "inr": '₹'
    };
    
    return (
        <div className="checkout-container">
        
        <CostInfo total = {total} currencySymbol={currencySymbol[currency]}/>
        
        {/* Payment section */}
        <div className="payment-section">
            <div className="payment-content">
            
            {/* <ApplePayment /> */}

            <div className="divider">
                <span>Pay with card</span>
            </div>

            <Elements stripe={stripePromise}>
                <CardPayment total={total} stripePromise = {stripePromise} currency={currency} setCurrency={setCurrency} currencySymbol={currencySymbol[currency]}/>
            </Elements>
            
            
    
            </div>
        </div>
        </div>
 )
}
