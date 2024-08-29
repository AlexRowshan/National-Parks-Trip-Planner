import React from 'react';

function Modal({ title, content, onConfirm, onCancel, showCancelButton = true, confirmButtonText = "Confirm", cancelButtonText = "Cancel" }) {
  return (
    <div className="modal-overlay">
      <div className="modal-content">
        <h2>{title}</h2>
        <p>{content}</p>
        <div className="button-container">
          <button onClick={onConfirm} className="modal-confirm-button">{confirmButtonText}</button>
          {showCancelButton && <button onClick={onCancel} className="modal-cancel-button">{cancelButtonText}</button>}
        </div>
      </div>
    </div>
  );
}

export default Modal;
