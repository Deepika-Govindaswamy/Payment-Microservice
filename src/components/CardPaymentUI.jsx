
import React, {useState} from 'react'
import {CardElement} from '@stripe/react-stripe-js';

export default function CardPaymentUI ({handlePaymentRequest, isProcessing, errorMessage, stripe, total, currency, setCurrency, 
                                        currencySymbol, email, setEmail, billingName, setBillingName, city, setCity
                                    }) 
{
    const cardStyleOptions = {
    style: {
        base: {
        fontSize: '16px',
        color: '#32325d',
        fontFamily: '"Helvetica Neue", Helvetica, sans-serif',
        '::placeholder': { color: '#a0aec0' },
        },
        invalid: {
        color: '#fa755a',
        iconColor: '#fa755a'
        }
    },
    hidePostalCode: true 
    };


    return (

        <div className="form-container">
            <h2>Billing information</h2>
            <div className="form-group">
                <label htmlFor="email">Email</label>
                <div className="input-with-icon">
                <input type="email" onChange={(e) => setEmail(e.target.value)} value={email} id="email" className="form-control" placeholder="Email" />
                <span className="input-icon">✉️</span>
                </div>
            </div>

            <h3>Billing address</h3>
            <div className="form-group">
                <input type="text" onChange={(e) => setBillingName(e.target.value)} value={billingName} placeholder="Name" className="form-control" />
            </div>
            <div className="form-group select-container">
                <select className="form-control" onChange={(e) => setCurrency(e.target.value)} value={currency}>
                <option value={"gbp"}>United Kingdom</option>
                {/* <option value={"inr"}>India</option>
                <option value={"usd"}>United States</option> */}
                </select>
            </div>
            <div className="form-group">
                <input type="text" onChange={(e) => setCity(e.target.value)} value={city} placeholder="Address" className="form-control" />
            </div>

            <h3>Payment details</h3>
            <div className="form-group">
                <label htmlFor="card-element">Card information</label>
                <CardElement id="card-element" options={cardStyleOptions} className="card-element-style" />
            </div>

            {errorMessage && (
                <div style={{ color: '#fa755a', marginBottom: '15px' }}>
                {errorMessage}
                </div>
            )}

            <button 
                className="pay-button" 
                onClick={handlePaymentRequest}
                disabled={isProcessing || !stripe}
            >
                {isProcessing ? 'Processing...' : `Pay ${currencySymbol + total.toFixed(2)}`}
            </button>
        </div>
    )
}
